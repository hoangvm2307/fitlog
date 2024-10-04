package com.example.fitlog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.model.Workout;
import com.example.fitlog.model.ExerciseSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class WorkoutDetailFragment extends Fragment {

    private static final String TAG = "WorkoutDetailFragment";
    private TextView workoutTitle;
    private TextView workoutDateTime;
    private TextView workoutDuration;
    private TextView totalVolume;
    private RecyclerView exerciseRecyclerView;
    private DatabaseHelper dbHelper;
    private ImageView backArrow;

    private static final String ARG_WORKOUT_ID = "workout_id";

    public static WorkoutDetailFragment newInstance(int workoutId) {
        WorkoutDetailFragment fragment = new WorkoutDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WORKOUT_ID, workoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_detail, container, false);

        backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        workoutTitle = view.findViewById(R.id.workoutTitle);
        workoutDateTime = view.findViewById(R.id.workoutDateTime);
        workoutDuration = view.findViewById(R.id.workoutDuration);
        totalVolume = view.findViewById(R.id.totalVolume);
        exerciseRecyclerView = view.findViewById(R.id.exerciseRecyclerView);

        dbHelper = DatabaseHelper.getInstance(getContext());

        if (getArguments() != null) {
            int workoutId = getArguments().getInt(ARG_WORKOUT_ID, -1);
            if (workoutId != -1) {
                Workout workout = dbHelper.getWorkoutById(workoutId);
                if (workout != null) {
                    displayWorkoutDetails(workout);
                } else {
                    Log.e(TAG, "Workout not found for id: " + workoutId);
                }
            } else {
                Log.e(TAG, "Invalid workout id");
            }
        } else {
            Log.e(TAG, "No arguments provided");
        }

        return view;
    }

    private void displayWorkoutDetails(Workout workout) {
        try {
            workoutTitle.setText(dbHelper.getWorkoutTemplateName(workout.getTemplateId()));
            
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM yyyy, HH:mm", Locale.getDefault());
            workoutDateTime.setText(sdf.format(workout.getStartTime()));
            
            long durationInMinutes = (workout.getEndTime().getTime() - workout.getStartTime().getTime()) / (60 * 1000);
            workoutDuration.setText(String.format(Locale.getDefault(), "%dh %dm", durationInMinutes / 60, durationInMinutes % 60));
            
            double totalVolumeKg = calculateTotalVolume(workout.getExerciseSets());
            totalVolume.setText(String.format(Locale.getDefault(), "%.1f kg", totalVolumeKg));

            ExerciseDetailsAdapter adapter = new ExerciseDetailsAdapter(groupExerciseSets(workout.getExerciseSets()), dbHelper);
            exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            exerciseRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error displaying workout details", e);
        }
    }

    private double calculateTotalVolume(List<ExerciseSet> exerciseSets) {
        double totalVolume = 0;
        for (ExerciseSet set : exerciseSets) {
            totalVolume += set.getWeight() * set.getReps();
        }
        return totalVolume;
    }

    private Map<Integer, List<ExerciseSet>> groupExerciseSets(List<ExerciseSet> exerciseSets) {
        Map<Integer, List<ExerciseSet>> groupedSets = new HashMap<>();
        for (ExerciseSet set : exerciseSets) {
            groupedSets.computeIfAbsent(set.getExerciseId(), k -> new ArrayList<>()).add(set);
        }
        return groupedSets;
    }
}