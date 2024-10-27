package com.example.fitlog;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.ExerciseSet;
import com.example.fitlog.model.Workout;
import com.example.fitlog.model.Template;
import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DAOs.WorkoutDAO;
import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.DAOs.ExerciseSetDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessionDetails extends Fragment {
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
    private int workoutId;

    public static SessionDetails newInstance(int templateId) {
        SessionDetails fragment = new SessionDetails();
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
        workoutDAO = new WorkoutDAO(dbHelper);
        exerciseDAO = new ExerciseDAO(dbHelper);
        templateDAO = new TemplateDAO(dbHelper);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_session_details, container, false);

        chronometer = view.findViewById(R.id.chronometer);
        finishButton = view.findViewById(R.id.finishButton);
        exercisesRecyclerView = view.findViewById(R.id.exercisesRecyclerView);

        // Start the chronometer
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        // Set up the RecyclerView
        exercises = getExercisesForTemplate(templateId);
        ExerciseAdapter adapter = new ExerciseAdapter(exercises);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        exercisesRecyclerView.setAdapter(adapter);

        // Create a new workout and save it to the database immediately
        Date startTime = new Date();
        currentWorkout = new Workout(0, 1, templateId, startTime, null); // Assuming user_id is 1 for now
        workoutId = (int) workoutDAO.insertWorkout(currentWorkout);
        if (workoutId == -1) {
            Toast.makeText(requireContext(), "Error creating workout", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return view;
        }
        currentWorkout.setId((int) workoutId);

        finishButton.setOnClickListener(v -> finishWorkout());

        return view;
    }

    private List<Exercise> getExercisesForTemplate(int templateId) {
        return exerciseDAO.getExercisesForTemplate(templateId);
    }

    private void finishWorkout() {
        currentWorkout.setEndTime(new Date());

        try {workoutDAO.updateWorkout(currentWorkout);

            Toast.makeText(requireContext(), "Workout finished successfully", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        } catch (Exception e){
            Toast.makeText(requireContext(), "Error finishing workout", Toast.LENGTH_SHORT).show();
        }
    }

    // private boolean saveAllSets() {
    //     ExerciseSetDAO exerciseSetDAO = new ExerciseSetDAO(DatabaseHelper.getInstance(requireContext()));
    //     boolean allSaved = true;

    //     for (Exercise exercise : exercises) {
    //         for (ExerciseSet set : exercise.getSets()) {
    //             set.setSessionId(currentWorkout.getId());
    //             long setId = exerciseSetDAO.createExerciseSet(set);
    //             if (setId == -1) {
    //                 allSaved = false;
    //                 Log.e("SessionDetails", "Failed to save set for exercise: " + exercise.getName());
    //             }
    //         }
    //     }

    //     return allSaved;
    // }

    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {
        private List<Exercise> exercises;

        ExerciseAdapter(List<Exercise> exercises) {
            this.exercises = exercises;
        }

        @Override
        public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_session, parent, false);
            return new ExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ExerciseViewHolder holder, int position) {
            Exercise exercise = exercises.get(position);
            holder.bind(exercise);
        }

        @Override
        public int getItemCount() {
            return exercises.size();
        }

        class ExerciseViewHolder extends RecyclerView.ViewHolder {
            TextView exerciseName;
            LinearLayout setsContainer;

            ExerciseViewHolder(View itemView) {
                super(itemView);
                exerciseName = itemView.findViewById(R.id.exerciseName);
                setsContainer = itemView.findViewById(R.id.setsContainer);
            }

            void bind(Exercise exercise) {
                exerciseName.setText(exercise.getName());
                setsContainer.removeAllViews();
                List<ExerciseSet> previousSets = workoutDAO.getLastWorkoutSets(exercise.getId());
                int setCount = Math.max(previousSets.size(), 3);

                for (int i = 0; i < setCount; i++) {
                    View setView = LayoutInflater.from(requireContext()).inflate(R.layout.item_set, setsContainer, false);
                    TextView setNumberTextView = setView.findViewById(R.id.setNumberTextView);
                    TextView previousSetTextView = setView.findViewById(R.id.previousSetTextView);
                    EditText weightEdit = setView.findViewById(R.id.weightEdit);
                    EditText repsEdit = setView.findViewById(R.id.repsEdit);
                    CheckBox setDoneCheckBox = setView.findViewById(R.id.setDoneCheckBox);

                    final int setNumber = i + 1;
                    setNumberTextView.setText(String.valueOf(setNumber));

                    if (i < previousSets.size()) {
                        ExerciseSet previousSet = previousSets.get(i);
                        previousSetTextView.setText(String.format("%.1f kg x %d", previousSet.getWeight(), previousSet.getReps()));
                    } else {
                        previousSetTextView.setText("0 kg x 10");
                    }

                    setDoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            String weightStr = weightEdit.getText().toString();
                            String repsStr = repsEdit.getText().toString();
                            if (!weightStr.isEmpty() && !repsStr.isEmpty()) {
                                float weight = Float.parseFloat(weightStr);
                                int reps = Integer.parseInt(repsStr);
                                ExerciseSet set = new ExerciseSet(0, workoutId, exercise.getId(), setNumber, weight, reps);
                                currentWorkout.addExerciseSet(set);
                                ExerciseSetDAO exerciseSetDAO = new ExerciseSetDAO(DatabaseHelper.getInstance(requireContext()));
                                long setId = exerciseSetDAO.createExerciseSet(set);
                                if (setId != -1) {
                                    Toast.makeText(requireContext(), "Set saved", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(requireContext(), "Error saving set", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(requireContext(), "Please enter weight and reps", Toast.LENGTH_SHORT).show();
                                setDoneCheckBox.setChecked(false);
                            }
                        }
                    });

                    setsContainer.addView(setView);
                }

                // Add "Add Set" button
                Button addSetButton = new Button(requireContext());
                addSetButton.setText("+ Add Set");
                addSetButton.setOnClickListener(v -> {
                    // Logic to add a new set
                    // This could be similar to the code above, but with a new set number
                });
                setsContainer.addView(addSetButton);
            }
        }
    }
}
