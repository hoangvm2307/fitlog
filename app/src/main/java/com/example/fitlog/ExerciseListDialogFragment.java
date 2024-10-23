package com.example.fitlog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.fitlog.model.Exercise;
import java.util.ArrayList;
import java.util.List;

public class ExerciseListDialogFragment extends DialogFragment {
    private static final String ARG_EXERCISE = "exercises";

    private ArrayList<Exercise> exerciseList;
    private ExerciseAdapter adapter;
    private OnExerciseSelectedListener listener;

    // Interface for exercise selection callback
    public interface OnExerciseSelectedListener {
        void onExerciseSelected(Exercise exercise);
    }

    // Interface for edit button callback
    public interface OnEditClickListener {
        void onEditClick();
    }

    public ExerciseListDialogFragment() {}

    public void setOnExerciseSelectedListener(OnExerciseSelectedListener listener) {
        this.listener = listener;
    }

    public static ExerciseListDialogFragment newInstance(List<Exercise> exercises) {
        ExerciseListDialogFragment fragment = new ExerciseListDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_EXERCISE, new ArrayList<>(exercises));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
        if (getArguments() != null) {
            exerciseList = getArguments().getParcelableArrayList(ARG_EXERCISE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_list_dialog, container, false);

        // Initialize views
        RecyclerView recyclerView = view.findViewById(R.id.exerciseRecyclerView);
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        TextView exerciseNameTitle = view.findViewById(R.id.exerciseName);

        // Set title
        exerciseNameTitle.setText("Select Exercise");

        // Set up the RecyclerView with your existing adapter
        adapter = new ExerciseAdapter(exerciseList, exercise -> {
            if (listener != null) {
                listener.onExerciseSelected(exercise);
            }
            dismiss();
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up click listeners
        closeButton.setOnClickListener(v -> dismiss());

        // Handle search functionality
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExercises(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        return view;
    }

    private void filterExercises(String query) {
        if (exerciseList == null) return;

        List<Exercise> filteredList = new ArrayList<>();
        for (Exercise exercise : exerciseList) {
            if (exercise.getName().toLowerCase().contains(query.toLowerCase()) ||
                    exercise.getBodypart().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(exercise);
            }
        }
        adapter.updateExercises(filteredList);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}