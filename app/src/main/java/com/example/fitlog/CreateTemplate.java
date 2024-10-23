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
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.DAOs.WorkoutDAO;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.Template;
import com.example.fitlog.model.Workout;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateTemplate extends Fragment implements ExerciseListDialogFragment.OnExerciseSelectedListener {
    private ExerciseAdapter adapter;
    private ExerciseDAO exerciseDAO;
    private List<Exercise> exercises;
    private List<Exercise> selectedExercises;

    private EditText etTemplateName;
    private EditText etNotes;

    private SelectedExerciseCreateAdapter selectedExerciseCreateAdapter;
    private RecyclerView rvSelectedExercises;
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

        // Khởi tạo RecyclerView cho selected exercises
        selectedExercises = new ArrayList<>();
        rvSelectedExercises = view.findViewById(R.id.rvSelectedExercises);

        // Tạo adapter với callback xóa item
        selectedExerciseCreateAdapter = new SelectedExerciseCreateAdapter(selectedExercises, position -> {
            selectedExercises.remove(position);
            selectedExerciseCreateAdapter.notifyItemRemoved(position);
        });

        etTemplateName = view.findViewById(R.id.etTemplateName);
        etNotes = view.findViewById(R.id.etNotes);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        Button btnAddExercise = view.findViewById(R.id.btnAddExercises);

        exercises= exerciseDAO.getAllExercises();
        btnAddExercise.setOnClickListener(v -> onAddExerciseClick(exercises));

        // Set up RecyclerView
        rvSelectedExercises.setAdapter(selectedExerciseCreateAdapter);
        rvSelectedExercises.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Thêm divider giữa các item nếu muốn
        rvSelectedExercises.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL));

        btnSave.setOnClickListener(v -> saveNewTemplate());

        return view;
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

    @Override
    public void onExerciseSelected(Exercise exercise) {
        // Kiểm tra xem exercise đã tồn tại chưa
        boolean isExisting = selectedExercises.stream()
                .anyMatch(e -> e.getId() == exercise.getId());

        if (!isExisting) {
            selectedExercises.add(exercise);
            selectedExerciseCreateAdapter.notifyItemInserted(selectedExercises.size() - 1);
            // Scroll đến item vừa thêm
            rvSelectedExercises.smoothScrollToPosition(selectedExercises.size() - 1);
        } else {
            Toast.makeText(requireContext(),
                    "This exercise is already in the list", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNewTemplate() {
        String templateName = etTemplateName.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // Validate input
        if (templateName.isEmpty()) {
            etTemplateName.setError("Template name is required");
            return;
        }

        // Create new Template object
        Template template = new Template(
                0, // id will be set by SQLite
                1, // assuming user_id is 1 for now - you should get this from your user session
                templateName,
                notes,
                "public", // default visibility
                LocalDateTime.now(),
                null // last_used is initially null for new template
        );

        // Get TemplateDAO instance and insert template with exercises
        TemplateDAO templateDAO = new TemplateDAO(dbHelper);
        long templateId = templateDAO.insertTemplates(template, selectedExercises);

        if (templateId != -1) {
            Toast.makeText(requireContext(), "Template saved successfully", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        } else {
            Toast.makeText(requireContext(), "Error saving template", Toast.LENGTH_SHORT).show();
        }
    }
}