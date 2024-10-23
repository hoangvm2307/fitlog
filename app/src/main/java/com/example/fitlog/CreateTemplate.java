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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateTemplate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTemplate extends Fragment {
    private static final String ARG_TEMPLATE_ID = "template_id";
    private int templateId;

    private EditText etTemplateName, etNotes;
    private Button addExercise;
    private MaterialButton btnSave;

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
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_template, container, false);

        AppCompatImageButton btnBack = view.findViewById(R.id.backButton);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        return view;
    }
}