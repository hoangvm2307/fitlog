package com.example.fitlog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.DAOs.ExerciseDAO;
import java.util.List;
import java.util.ArrayList;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.Toast;

public class ExerciseList extends Fragment implements ExerciseAdapter.OnExerciseClickListener {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ExerciseDAO exerciseDAO;
    private List<Exercise> allExercises;
    private Button bodyPartButton;
    private TextView newButton;
    private EditText searchEditText;
    private String currentBodyPart = "All"; // Thêm biến để theo dõi body part hiện tại

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_exercise_list, container, false);

        recyclerView = view.findViewById(R.id.exerciseRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        exerciseDAO = new ExerciseDAO(getContext());
        allExercises = exerciseDAO.getAllExercises();

        adapter = new ExerciseAdapter(allExercises, this);
        recyclerView.setAdapter(adapter);

        bodyPartButton = view.findViewById(R.id.bodyPartButton);
        bodyPartButton.setOnClickListener(v -> showBodyPartMenu());

        newButton = view.findViewById(R.id.newButton);
        newButton.setOnClickListener(v -> openNewExerciseDialog());

        searchEditText = view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExercises(currentBodyPart, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterExercises(String bodyPart, String searchQuery) {
        List<Exercise> filteredList = new ArrayList<>();
        allExercises = exerciseDAO.getAllExercises();

        // Toast để hiển thị các tham số đầu vào
        Toast.makeText(getContext(),
                "Filtering - Body Part: '" + bodyPart + "', Search: '" + searchQuery + "'",
                Toast.LENGTH_SHORT).show();

        // Đầu tiên lọc theo body part
        List<Exercise> bodyPartFilteredList;
        if (bodyPart.equals("All")) {
            bodyPartFilteredList = new ArrayList<>(allExercises);
            Toast.makeText(getContext(),
                    "All exercises: " + allExercises.size(),
                    Toast.LENGTH_SHORT).show();
        } else {
            bodyPartFilteredList = new ArrayList<>();
            for (Exercise exercise : allExercises) {
                if (exercise.getBodypart().equals(bodyPart)) {
                    bodyPartFilteredList.add(exercise);
                }
            }
            Toast.makeText(getContext(),
                    "Exercises for " + bodyPart + ": " + bodyPartFilteredList.size(),
                    Toast.LENGTH_SHORT).show();
        }

        // Sau đó lọc theo search query
        if (searchQuery.isEmpty()) {
            filteredList = bodyPartFilteredList;
            Toast.makeText(getContext(),
                    "Empty search, showing all: " + filteredList.size(),
                    Toast.LENGTH_SHORT).show();
        } else {
            for (Exercise exercise : bodyPartFilteredList) {
                String exerciseName = exercise.getName().toLowerCase();
                String query = searchQuery.toLowerCase();

                // Toast để hiện từng so sánh
                Toast.makeText(getContext(),
                        "Compare: '" + exerciseName + "' with '" + query + "'",
                        Toast.LENGTH_SHORT).show();

                if (exerciseName.contains(query)) {
                    filteredList.add(exercise);
                    Toast.makeText(getContext(),
                            "Match found: " + exercise.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Toast kết quả cuối cùng
        Toast.makeText(getContext(),
                "Final results: " + filteredList.size(),
                Toast.LENGTH_SHORT).show();

        adapter.updateExercises(filteredList);
    }

    private void showBodyPartMenu() {
        PopupMenu popup = new PopupMenu(getContext(), bodyPartButton);
        popup.getMenu().add("All");
        for (String bodyPart : getUniqueBodyParts()) {
            popup.getMenu().add(bodyPart);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                currentBodyPart = item.getTitle().toString();
                bodyPartButton.setText(currentBodyPart);
                filterExercises(currentBodyPart, searchEditText.getText().toString());
                return true;
            }
        });

        popup.show();
    }

    // Các phương thức khác giữ nguyên
    private List<String> getUniqueBodyParts() {
        List<String> bodyParts = new ArrayList<>();
        for (Exercise exercise : allExercises) {
            if (!bodyParts.contains(exercise.getBodypart())) {
                bodyParts.add(exercise.getBodypart());
            }
        }
        return bodyParts;
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        ExerciseDetailsDialogFragment detailsFragment = ExerciseDetailsDialogFragment.newInstance(exercise);
        detailsFragment.show(getParentFragmentManager(), "exercise_details");
    }

    private void openNewExerciseDialog() {
        NewExerciseDialogFragment newExerciseFragment = new NewExerciseDialogFragment();
        newExerciseFragment.setOnExerciseAddedListener(new NewExerciseDialogFragment.OnExerciseAddedListener() {
            @Override
            public void onExerciseAdded() {
                updateExerciseList();
            }
        });
        newExerciseFragment.show(getParentFragmentManager(), "new_exercise");
    }

    public void updateExerciseList() {
        allExercises = exerciseDAO.getAllExercises();
        filterExercises(currentBodyPart, searchEditText.getText().toString());
    }
}