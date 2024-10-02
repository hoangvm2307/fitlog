package com.example.fitlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ExerciseList extends Fragment {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ExerciseDAO exerciseDAO;
    private List<Exercise> allExercises;
    private Button bodyPartButton;

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

        adapter = new ExerciseAdapter(allExercises);
        recyclerView.setAdapter(adapter);

        bodyPartButton = view.findViewById(R.id.bodyPartButton);
        bodyPartButton.setOnClickListener(v -> showBodyPartMenu());
        
        return view;
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
                filterExercises(item.getTitle().toString());
                return true;
            }
        });

        popup.show();
    }

    private List<String> getUniqueBodyParts() {
        List<String> bodyParts = new ArrayList<>();
        for (Exercise exercise : allExercises) {
            if (!bodyParts.contains(exercise.getBodypart())) {
                bodyParts.add(exercise.getBodypart());
            }
        }
        return bodyParts;
    }

    private void filterExercises(String bodyPart) {
        List<Exercise> filteredList;
        if (bodyPart.equals("All")) {
            filteredList = new ArrayList<>(allExercises);
        } else {
            filteredList = new ArrayList<>();
            for (Exercise exercise : allExercises) {
                if (exercise.getBodypart().equals(bodyPart)) {
                    filteredList.add(exercise);
                }
            }
        }
        adapter.updateExercises(filteredList);
        bodyPartButton.setText(bodyPart);
    }
}