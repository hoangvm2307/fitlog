package com.example.fitlog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.model.ExerciseSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExerciseDetailsAdapter extends RecyclerView.Adapter<ExerciseDetailsAdapter.ExerciseViewHolder> {

    private List<Map.Entry<Integer, List<ExerciseSet>>> exerciseEntries;
    private DatabaseHelper dbHelper;

    public ExerciseDetailsAdapter(Map<Integer, List<ExerciseSet>> groupedSets, DatabaseHelper dbHelper) {
        this.exerciseEntries = new ArrayList<>(groupedSets.entrySet());
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_detail, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Map.Entry<Integer, List<ExerciseSet>> entry = exerciseEntries.get(position);
        int exerciseId = entry.getKey();
        List<ExerciseSet> sets = entry.getValue();

        holder.exerciseName.setText(dbHelper.getExerciseDAO().getExerciseName(exerciseId));
        
        StringBuilder setsBuilder = new StringBuilder();
        StringBuilder oneRMsBuilder = new StringBuilder();
        
        for (int i = 0; i < sets.size(); i++) {
            ExerciseSet set = sets.get(i);
            float oneRM = calculateOneRM(set.getWeight(), set.getReps());
            
            setsBuilder.append(String.format("%d: %.1f kg × %d\n", i + 1, set.getWeight(), set.getReps()));
            oneRMsBuilder.append(String.format("%.1f kg\n", oneRM));
        }
        
        holder.exerciseSets.setText(setsBuilder.toString().trim());
        holder.exerciseOneRMs.setText(oneRMsBuilder.toString().trim());
    }

    @Override
    public int getItemCount() {
        return exerciseEntries.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView exerciseSets;
        TextView exerciseOneRMs;

        ExerciseViewHolder(View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseSets = itemView.findViewById(R.id.exerciseSets);
            exerciseOneRMs = itemView.findViewById(R.id.exerciseOneRMs);
        }
    }

    private float calculateOneRM(float weight, int reps) {
        return weight * (36 / (37 - reps));
    }
}