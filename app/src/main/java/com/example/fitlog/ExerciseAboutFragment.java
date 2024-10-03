package com.example.fitlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitlog.model.Exercise;

public class ExerciseAboutFragment extends Fragment {
    private static final String ARG_EXERCISE = "exercise";

    private Exercise exercise;

    public static ExerciseAboutFragment newInstance(Exercise exercise) {
        ExerciseAboutFragment fragment = new ExerciseAboutFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EXERCISE, exercise);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exercise = getArguments().getParcelable(ARG_EXERCISE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_about, container, false);

        ImageView exerciseImage = view.findViewById(R.id.exerciseImage);
        TextView instructionsText = view.findViewById(R.id.instructionsText);


        if (exercise != null) {
            int resourceId = getResources().getIdentifier(
                    exercise.getImageName(), "drawable", requireContext().getPackageName());
            if (resourceId != 0) {
                exerciseImage.setImageResource(resourceId);
            }
            instructionsText.setText(exercise.getInstruction());

        }

        return view;
    }
}