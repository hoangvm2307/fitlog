package com.example.fitlog;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;

import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.DAOs.WorkoutDAO;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.Workout;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CreateTemplate extends Fragment {
    private ExerciseAdapter adapter;
    private ExerciseDAO exerciseDAO;
    private List<Exercise> exercises;
    private DatabaseHelper dbHelper;

    public static CreateTemplate newInstance() {
        CreateTemplate fragment = new CreateTemplate();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CreateTemplate() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());
        exerciseDAO = new ExerciseDAO(dbHelper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_template, container, false);

        AppCompatImageButton btnBack = view.findViewById(R.id.backButton);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        EditText etTemplateName = view.findViewById(R.id.etTemplateName);
        EditText etNotes = view.findViewById(R.id.etNotes);
        Button btnAddExercise = view.findViewById(R.id.btnAddExercises);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        exercises= exerciseDAO.getAllExercises();
        btnAddExercise.setOnClickListener(v -> onAddExerciseClick(exercises));
        return view;
    }

    public void onAddExerciseClick(List<Exercise> exercises) {
        ExerciseListDialogFragment listDialogFragmentFragment = ExerciseListDialogFragment.newInstance(exercises);
        listDialogFragmentFragment.show(getParentFragmentManager(), "exercise_list");
    }
}