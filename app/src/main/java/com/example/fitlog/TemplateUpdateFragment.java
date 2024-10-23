package com.example.fitlog;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.Template;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TemplateUpdateFragment extends Fragment {

    private static final String ARG_TEMPLATE_ID = "template_id";

    private int templateId;
    private TemplateDAO templateDAO;
    private ExerciseDAO exerciseDAO;
    private SelectedExerciseAdapter adapter;

    public TemplateUpdateFragment() {}
    public static TemplateUpdateFragment newInstance(int templateId) {
        TemplateUpdateFragment fragment = new TemplateUpdateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TEMPLATE_ID, templateId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            templateId = getArguments().getInt(ARG_TEMPLATE_ID);
        }
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(requireContext());
        templateDAO = new TemplateDAO(dbHelper);
        exerciseDAO = new ExerciseDAO(dbHelper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_template_update, container, false);

        AppCompatImageButton btnBack = view.findViewById(R.id.backButton);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        Button btnAdd = view.findViewById(R.id.btnAddExercises);
        TextView templateName = view.findViewById(R.id.etTemplateName);
        TextView templateNote = view.findViewById(R.id.etNotes);
        RecyclerView listExercise = view.findViewById(R.id.rvSelectedExercises);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        Template template = templateDAO.getTemplateById(templateId);
        if (template != null) {
            templateName.setText(template.getTitle());
            templateNote.setText("Last performed: " + template.getFormattedLastUsed());

            List<Exercise> exercises = exerciseDAO.getExercisesForTemplate(templateId);
            adapter = new SelectedExerciseAdapter(exercises, position -> {
                exercises.remove(position);
                adapter.notifyItemRemoved(position);
            });
            listExercise.setLayoutManager(new LinearLayoutManager(requireContext()));
            listExercise.setAdapter(adapter);
        }

        return view;
    }
}