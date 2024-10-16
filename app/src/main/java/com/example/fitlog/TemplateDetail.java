package com.example.fitlog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.Template;

import java.util.ArrayList;
import java.util.List;

public class TemplateDetail extends Fragment {

    private static final String ARG_TEMPLATE_ID = "template_id";

    private int templateId;
    private TemplateDAO templateDAO;
    private ExerciseDAO exerciseDAO;
    private RecyclerView exercisesRecyclerView;
    private TextView titleTextView;
    private TextView lastPerformedTextView;
    private Button startWorkoutButton;

    public static TemplateDetail newInstance(int templateId) {
        TemplateDetail fragment = new TemplateDetail();
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
        View view = inflater.inflate(R.layout.fragment_template_detail, container, false);

        titleTextView = view.findViewById(R.id.titleTextView);
        lastPerformedTextView = view.findViewById(R.id.lastPerformedTextView);
        exercisesRecyclerView = view.findViewById(R.id.exercisesRecyclerView);
        startWorkoutButton = view.findViewById(R.id.startWorkoutButton);

        view.findViewById(R.id.backButton).setOnClickListener(v -> requireActivity().onBackPressed());

        Template template = templateDAO.getTemplateById(templateId);
        if (template != null) {
            titleTextView.setText(template.getTitle());
            lastPerformedTextView.setText("Last performed: " + template.getFormattedLastUsed());

            List<Exercise> exercises = exerciseDAO.getExercisesForTemplate(templateId);
            TemplateExerciseAdapter adapter = new TemplateExerciseAdapter(exercises);
            exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            exercisesRecyclerView.setAdapter(adapter);
        }

        startWorkoutButton.setOnClickListener(v -> {
            SessionDetails sessionDetailsFragment = SessionDetails.newInstance(templateId);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, sessionDetailsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private class TemplateExerciseAdapter extends RecyclerView.Adapter<TemplateExerciseAdapter.ViewHolder> {
        private List<Exercise> exercises;

        TemplateExerciseAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_template_exercise, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Exercise exercise = exercises.get(position);
            holder.nameTextView.setText(exercise.getName());
            holder.bodyPartTextView.setText(exercise.getBodypart());
            holder.setsTextView.setText(exercise.getExerciseOrder() + " x");

            if (exercise.getImageName() != null) {
                int resourceId = getResources().getIdentifier(
                    exercise.getImageName(),
                    "drawable",
                    requireContext().getPackageName()
                );
                if (resourceId != 0) {
                    holder.exerciseImage.setImageResource(resourceId);
                } else {
                    holder.exerciseImage.setImageResource(R.drawable.default_exercise_image);
                }
            } else {
                holder.exerciseImage.setImageResource(R.drawable.default_exercise_image);
            }
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView exerciseImage;
            TextView nameTextView;
            TextView bodyPartTextView;
            TextView setsTextView;

            ViewHolder(View itemView) {
                super(itemView);
                exerciseImage = itemView.findViewById(R.id.exerciseImage);
                nameTextView = itemView.findViewById(R.id.exerciseNameTextView);
                bodyPartTextView = itemView.findViewById(R.id.exerciseBodyPartTextView);
                setsTextView = itemView.findViewById(R.id.exerciseSetsTextView);
            }
        }
    }
}
