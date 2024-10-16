package com.example.fitlog;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.example.fitlog.model.Workout;
import com.example.fitlog.model.ExerciseSet;
import com.example.fitlog.DatabaseHelper;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_MONTH = 0;
    private static final int TYPE_WORKOUT = 1;

    private List<Object> items = new ArrayList<>();
    private DatabaseHelper dbHelper;

    public HistoryAdapter(Map<String, List<Workout>> workoutsByMonth, Context context) {
        for (Map.Entry<String, List<Workout>> entry : workoutsByMonth.entrySet()) {
            items.add(new MonthItem(entry.getKey(), entry.getValue().size()));
            items.addAll(entry.getValue());
        }
        Log.d("HistoryAdapter", "Total items: " + items.size());
        dbHelper = DatabaseHelper.getInstance(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_MONTH) {
            View view = inflater.inflate(R.layout.item_month, parent, false);
            return new MonthViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_workout, parent, false);
            return new WorkoutViewHolder(view, dbHelper);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MonthViewHolder) {
            MonthItem monthItem = (MonthItem) items.get(position);
            ((MonthViewHolder) holder).bind(monthItem);
        } else if (holder instanceof WorkoutViewHolder) {
            Workout workout = (Workout) items.get(position);
            ((WorkoutViewHolder) holder).bind(workout);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof MonthItem ? TYPE_MONTH : TYPE_WORKOUT;
    }

    static class MonthViewHolder extends RecyclerView.ViewHolder {
        TextView monthText;

        MonthViewHolder(View itemView) {
            super(itemView);
            monthText = itemView.findViewById(R.id.monthText);
        }

        void bind(MonthItem monthItem) {
            monthText.setText(String.format("%s (%d workouts)", monthItem.month, monthItem.workoutCount));
        }
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutName, workoutDate, sessionTime, totalVolume;
        LinearLayout exerciseListContainer;
        private DatabaseHelper dbHelper;

        WorkoutViewHolder(View itemView, DatabaseHelper dbHelper) {
            super(itemView);
            workoutName = itemView.findViewById(R.id.workoutName);
            workoutDate = itemView.findViewById(R.id.workoutDate);
            sessionTime = itemView.findViewById(R.id.sessionTime);
            totalVolume = itemView.findViewById(R.id.totalVolume);
            exerciseListContainer = itemView.findViewById(R.id.exerciseListContainer);
            this.dbHelper = dbHelper;
        }

        void bind(Workout workout) {
            workoutName.setText(getWorkoutName(workout.getTemplateId()));
            
            // Format date with full month name
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM", Locale.getDefault());
            workoutDate.setText(sdf.format(workout.getStartTime()));
            
            sessionTime.setText(getSessionTime(workout.getStartTime(), workout.getEndTime()));
            totalVolume.setText(String.format("%.1f kg", calculateTotalVolume(workout.getExerciseSets())));
            
            exerciseListContainer.removeAllViews();
            Map<Integer, Integer> exerciseSetCounts = new HashMap<>();
            Map<Integer, ExerciseSet> bestSets = new HashMap<>();

            for (ExerciseSet set : workout.getExerciseSets()) {
                exerciseSetCounts.put(set.getExerciseId(), exerciseSetCounts.getOrDefault(set.getExerciseId(), 0) + 1);
                if (!bestSets.containsKey(set.getExerciseId()) || 
                    set.getWeight() * set.getReps() > bestSets.get(set.getExerciseId()).getWeight() * bestSets.get(set.getExerciseId()).getReps()) {
                    bestSets.put(set.getExerciseId(), set);
                }
            }

            for (Map.Entry<Integer, ExerciseSet> entry : bestSets.entrySet()) {
                LinearLayout exerciseRow = new LinearLayout(itemView.getContext());
                exerciseRow.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                exerciseRow.setOrientation(LinearLayout.HORIZONTAL);

                TextView exerciseName = new TextView(itemView.getContext());
                exerciseName.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                exerciseName.setText(String.format("%dx %s", exerciseSetCounts.get(entry.getKey()), getExerciseName(entry.getKey())));

                TextView bestSet = new TextView(itemView.getContext());
                bestSet.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                bestSet.setGravity(Gravity.END);
                float weight = entry.getValue().getWeight();
                String weightStr = (weight % 1 == 0) ? String.format("%.0f", weight) : String.format("%.1f", weight);
                bestSet.setText(String.format("%skg x %d", weightStr, entry.getValue().getReps()));

                exerciseRow.addView(exerciseName);
                exerciseRow.addView(bestSet);

                exerciseListContainer.addView(exerciseRow);

                // Add some vertical spacing between rows
                View spacer = new View(itemView.getContext());
                spacer.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 
                        (int) (8 * itemView.getResources().getDisplayMetrics().density)));
                exerciseListContainer.addView(spacer);
            }

            itemView.setOnClickListener(v -> {
                ((MainActivity) itemView.getContext()).navigateToWorkoutDetail(workout.getId());
            });
        }

        private String getSessionTime(Date startTime, Date endTime) {
            long durationInMillis = endTime.getTime() - startTime.getTime();
            long minutes = durationInMillis / (60 * 1000);
            return String.format(Locale.getDefault(), "%d min", minutes);
        }

        private double calculateTotalVolume(List<ExerciseSet> exerciseSets) {
            double totalVolume = 0;
            for (ExerciseSet set : exerciseSets) {
                totalVolume += set.getWeight() * set.getReps();
            }
            return totalVolume;
        }

        private String getWorkoutName(int templateId) {
            return dbHelper.getWorkoutDAO().getWorkoutTemplateName(templateId);
        }

        private String getExerciseName(int exerciseId) {
            return dbHelper.getExerciseDAO().getExerciseName(exerciseId);
        }
    }

    static class MonthItem {
        String month;
        int workoutCount;

        MonthItem(String month, int workoutCount) {
            this.month = month;
            this.workoutCount = workoutCount;
        }
    }
}