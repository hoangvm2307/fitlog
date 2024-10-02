package com.example.fitlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DividerItemDecoration;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.DAOs.ExerciseDAO;
import java.util.List;

public class ExerciseList extends Fragment {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private ExerciseDAO exerciseDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_exercise_list, container, false);

        recyclerView = view.findViewById(R.id.exerciseRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Thêm đường kẻ phân chia
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        exerciseDAO = new ExerciseDAO(getContext());
        exerciseDAO.seedExercises();
        List<Exercise> exercises = exerciseDAO.getAllExercises();

        adapter = new ExerciseAdapter(exercises);
        recyclerView.setAdapter(adapter);
        
        return view;
    }
}