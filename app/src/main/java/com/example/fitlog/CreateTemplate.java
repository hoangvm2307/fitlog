package com.example.fitlog;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;

import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.DAOs.WorkoutDAO;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.Workout;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateTemplate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTemplate extends Fragment {
    private static final String ARG_TEMPLATE_ID = "template_id";
    private int templateId;

    private Chronometer chronometer;
    private Button finishButton;
    private RecyclerView exercisesRecyclerView;
    private WorkoutDAO workoutDAO;
    private ExerciseDAO exerciseDAO;
    private TemplateDAO templateDAO;
    private List<Exercise> exercises;
    private Workout currentWorkout;

    public static CreateTemplate newInstance() {
        CreateTemplate fragment = new CreateTemplate();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CreateTemplate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateTemplate.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateTemplate newInstance(String param1, String param2) {
        CreateTemplate fragment = new CreateTemplate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_template, container, false);
    }
}