package com.example.fitlog;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.Template;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TemplateUpdateFragment extends Fragment implements ExerciseListDialogFragment.OnExerciseSelectedListener {

    private static final String ARG_TEMPLATE_ID = "template_id";

    private int templateId;
    private TemplateDAO templateDAO;
    private ExerciseDAO exerciseDAO;
    private SelectedExerciseCreateAdapter adapter;

    private List<Exercise> exerciseList;
    private List<Exercise> exercises;
    private EditText templateName;
    private EditText templateNote;

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
        templateName = view.findViewById(R.id.etTemplateName);
        templateNote = view.findViewById(R.id.etNotes);
        RecyclerView listExercise = view.findViewById(R.id.rvSelectedExercises);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        exerciseList = exerciseDAO.getAllExercises();
        btnAdd.setOnClickListener(v -> onAddExerciseClick(exerciseList));

        Template template = templateDAO.getTemplateById(templateId);
        if (template != null) {
            templateName.setText(template.getTitle());
            templateNote.setText("Last performed: " + template.getFormattedLastUsed());

            exercises = exerciseDAO.getExercisesForTemplate(templateId);
            adapter = new SelectedExerciseCreateAdapter(exercises, position -> {
                exercises.remove(position);
                adapter.notifyItemRemoved(position);
            });
            listExercise.setLayoutManager(new LinearLayoutManager(requireContext()));
            listExercise.setAdapter(adapter);

            // Thêm divider giữa các item nếu muốn
            listExercise.addItemDecoration(new DividerItemDecoration(requireContext(),
                    DividerItemDecoration.VERTICAL));
        }

        btnSave.setOnClickListener(v -> updateTemplate());

        return view;
    }

    @Override
    public void onExerciseSelected(Exercise exercise) {
        // Kiểm tra xem exercise đã tồn tại chưa
        boolean isExisting = exercises.stream()
                .anyMatch(e -> e.getId() == exercise.getId());

        if (!isExisting) {
            exercises.add(exercise);
            adapter.notifyItemInserted(exercises.size() - 1);
        } else {
            Toast.makeText(requireContext(),
                    "This exercise is already in the list", Toast.LENGTH_SHORT).show();
        }
    }

    public void onAddExerciseClick(List<Exercise> exercises) {
        if (exercises == null || exercises.isEmpty()) {
            Toast.makeText(requireContext(), "No exercises available", Toast.LENGTH_SHORT).show();
            return;
        }
        ExerciseListDialogFragment listDialogFragmentFragment = ExerciseListDialogFragment.newInstance(exercises);
        listDialogFragmentFragment.setOnExerciseSelectedListener(this);
        listDialogFragmentFragment.show(getParentFragmentManager(), "exercise_list");
    }

    private void updateTemplate() {
        String tpName = templateName.getText().toString().trim();
        String notes = templateNote.getText().toString().trim();

        // Validate input
        if (tpName.isEmpty()) {
            templateName.setError("Template name is required");
            return;
        }

        // Get current template
        Template currentTemplate = templateDAO.getTemplateById(templateId);
        if (currentTemplate == null) {
            Toast.makeText(requireContext(), "Template not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update template object
        Template updatedTemplate = new Template(
                templateId,  // use existing template id
                currentTemplate.getUserId(), // keep existing user id
                tpName,
                notes,
                currentTemplate.getVisibility(), // keep existing visibility
                currentTemplate.getCreateAt(),  // keep existing creation time
                currentTemplate.getLastUsed()    // keep existing last used time
        );

        // Update template and its exercises
        boolean success = templateDAO.updateTemplate(updatedTemplate, exercises);

        if (success) {
            Toast.makeText(requireContext(), "Template updated successfully", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        } else {
            Toast.makeText(requireContext(), "Error updating template", Toast.LENGTH_SHORT).show();
        }
    }
}