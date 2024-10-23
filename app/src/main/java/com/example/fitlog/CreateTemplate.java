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
import com.example.fitlog.model.Workout;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CreateTemplate extends Fragment implements ExerciseListDialogFragment.OnExerciseSelectedListener {
    private ExerciseAdapter adapter;
    private ExerciseDAO exerciseDAO;
    private List<Exercise> exercises;
    private List<Exercise> selectedExercises;
    private SelectedExerciseAdapter selectedExerciseAdapter;
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
        selectedExerciseAdapter = new SelectedExerciseAdapter(selectedExercises, position -> {
            selectedExercises.remove(position);
            selectedExerciseAdapter.notifyItemRemoved(position);
        });

        EditText etTemplateName = view.findViewById(R.id.etTemplateName);
        EditText etNotes = view.findViewById(R.id.etNotes);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        Button btnAddExercise = view.findViewById(R.id.btnAddExercises);

        exercises= exerciseDAO.getAllExercises();
        btnAddExercise.setOnClickListener(v -> onAddExerciseClick(exercises));

        // Set up RecyclerView
        rvSelectedExercises.setAdapter(selectedExerciseAdapter);
        rvSelectedExercises.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Thêm divider giữa các item nếu muốn
        rvSelectedExercises.addItemDecoration(new DividerItemDecoration(requireContext(),
                DividerItemDecoration.VERTICAL));

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
            selectedExerciseAdapter.notifyItemInserted(selectedExercises.size() - 1);
            // Scroll đến item vừa thêm
            rvSelectedExercises.smoothScrollToPosition(selectedExercises.size() - 1);
        } else {
            Toast.makeText(requireContext(),
                    "This exercise is already in the list", Toast.LENGTH_SHORT).show();
        }
    }

    // Thêm method để lưu template khi cần
    private void saveTemplate() {
        // Validate
        if (selectedExercises.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Please add at least one exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logic lưu template với các exercise đã chọn
    }
}