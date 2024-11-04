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
import androidx.appcompat.app.AlertDialog;

import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.ExerciseSet;
import com.example.fitlog.model.Workout;
import com.example.fitlog.model.Template;
import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DAOs.WorkoutDAO;
import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.DAOs.ExerciseSetDAO;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

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
    private Date startTime;
    private int workoutId;
    private TextView workoutNameTextView;

    // Add new field to store temporary exercise set data
    private Map<Integer, List<PendingSet>> pendingSets = new HashMap<>();

    // Create a class to hold pending set data
    private static class PendingSet {
        int exerciseId;
        int setNumber;
        float weight;
        int reps;

        PendingSet(int exerciseId, int setNumber, float weight, int reps) {
            this.exerciseId = exerciseId;
            this.setNumber = setNumber;
            this.weight = weight;
            this.reps = reps;
        }
    }

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
        startTime = new Date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_session_details, container, false);

        chronometer = view.findViewById(R.id.chronometer);
        finishButton = view.findViewById(R.id.finishButton);
        exercisesRecyclerView = view.findViewById(R.id.exercisesRecyclerView);
        workoutNameTextView = view.findViewById(R.id.workoutNameTextView);
        workoutNameTextView.setText(templateDAO.getTemplateById(templateId).getTitle());

        // Start the chronometer
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        // Set up the RecyclerView

        exercises = getExercisesForTemplate(templateId);
        ExerciseAdapter adapter = new ExerciseAdapter(exercises);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        exercisesRecyclerView.setAdapter(adapter);

        finishButton.setOnClickListener(v -> finishWorkout());

        return view;
    }

    private List<Exercise> getExercisesForTemplate(int templateId) {
        return exerciseDAO.getExercisesForTemplate(templateId);
    }

    private void finishWorkout() {
        // Check if there are any pending sets
        boolean hasAnySets = false;
        for (List<PendingSet> sets : pendingSets.values()) {
            if (!sets.isEmpty()) {
                hasAnySets = true;
                break;
            }
        }

        if (!hasAnySets) {
            // Show confirmation dialog
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("No Sets Recorded")
                .setMessage("You haven't recorded any sets. Do you want to cancel this workout?")
                .setPositiveButton("Cancel", (dialog, which) -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Return", null)
                .show();
            return;
        }

        // Rest of the existing finishWorkout logic
        Date currentEndTime = new Date();
        long durationInMillis = currentEndTime.getTime() - startTime.getTime();
        
        Date endTime;
        if (durationInMillis < 60000) {
            endTime = new Date(startTime.getTime() + 60000);
        } else {
            endTime = currentEndTime;
        }

        Workout workout = new Workout(0, 1, templateId, startTime, endTime);
        
        try {
            // Save workout
            int workoutId = (int) workoutDAO.insertWorkout(workout);
            if (workoutId == -1) {
                throw new Exception("Failed to save workout");
            }

            // Convert Date to LocalDateTime for template update
            LocalDateTime localEndTime = endTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

            // Update template's lastUsed timestamp
            templateDAO.updateTemplateLastUsed(templateId, localEndTime);

            // Save exercise sets
            ExerciseSetDAO exerciseSetDAO = new ExerciseSetDAO(DatabaseHelper.getInstance(requireContext()));
            for (List<PendingSet> sets : pendingSets.values()) {
                for (PendingSet pendingSet : sets) {
                    ExerciseSet set = new ExerciseSet(
                        0, 
                        workoutId,
                        pendingSet.exerciseId,
                        pendingSet.setNumber,
                        pendingSet.weight,
                        pendingSet.reps
                    );
                    long setId = exerciseSetDAO.createExerciseSet(set);
                    if (setId == -1) {
                        throw new Exception("Failed to save exercise set");
                    }
                }
            }

            Toast.makeText(requireContext(), "Workout saved successfully", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

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
                
                List<ExerciseSet> previousSets = workoutDAO.getLastTemplateExerciseSets(templateId, exercise.getId());
                Collections.sort(previousSets, (a, b) -> Integer.compare(a.getSetNumber(), b.getSetNumber()));
                int setCount = previousSets.isEmpty() ? 1 : previousSets.size();

                List<CheckBox> checkBoxes = new ArrayList<>();
                List<EditText> weightEdits = new ArrayList<>();
                List<EditText> repsEdits = new ArrayList<>();

                // Add initial sets in correct order
                for (int i = 0; i < setCount; i++) {
                    View setView = LayoutInflater.from(requireContext()).inflate(R.layout.item_set, setsContainer, false);
                    TextView setNumberTextView = setView.findViewById(R.id.setNumberTextView);
                    TextView previousSetTextView = setView.findViewById(R.id.previousSetTextView);
                    EditText weightEdit = setView.findViewById(R.id.weightEdit);
                    EditText repsEdit = setView.findViewById(R.id.repsEdit);
                    CheckBox setDoneCheckBox = setView.findViewById(R.id.setDoneCheckBox);

                    int setNumber = i + 1;  // Sets are 1-based
                    setNumberTextView.setText(String.valueOf(setNumber));

                    if (i < previousSets.size()) {
                        ExerciseSet previousSet = previousSets.get(i);
                        previousSetTextView.setText(String.format("%.1f kg x %d", previousSet.getWeight(), previousSet.getReps()));
                        
                        // Pre-fill the weight and reps inputs with previous values
                        weightEdit.setText(String.format(Locale.getDefault(), "%.1f", previousSet.getWeight()));
                        repsEdit.setText(String.valueOf(previousSet.getReps()));
                    } 
                     else {
                         previousSetTextView.setText("0 kg x 0");
//                         weightEdit.setText("0");
//                         repsEdit.setText("0");
                     }

                    weightEdits.add(weightEdit);
                    repsEdits.add(repsEdit);
                    checkBoxes.add(setDoneCheckBox);

                    // Disable all sets except the first one initially
                    boolean isFirstSet = setNumber == 1;
                    setDoneCheckBox.setEnabled(isFirstSet);
                    weightEdit.setEnabled(isFirstSet);
                    repsEdit.setEnabled(isFirstSet);

                    final int finalSetNumber = setNumber;
                    setDoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            String weightStr = weightEdit.getText().toString();
                            String repsStr = repsEdit.getText().toString();
                            
                            if (!weightStr.isEmpty() && !repsStr.isEmpty()) {
                                float weight = Float.parseFloat(weightStr);
                                int reps = Integer.parseInt(repsStr);
                                
                                if (!pendingSets.containsKey(exercise.getId())) {
                                    pendingSets.put(exercise.getId(), new ArrayList<>());
                                }
                                pendingSets.get(exercise.getId()).add(
                                    new PendingSet(exercise.getId(), finalSetNumber, weight, reps)
                                );

                                // Enable next set if it exists
                                if (finalSetNumber < checkBoxes.size()) {
                                    checkBoxes.get(finalSetNumber).setEnabled(true);
                                    weightEdits.get(finalSetNumber).setEnabled(true);
                                    repsEdits.get(finalSetNumber).setEnabled(true);
                                }

                                weightEdit.setEnabled(false);
                                repsEdit.setEnabled(false);
                            } else {
                                Toast.makeText(requireContext(), "Please enter weight and reps", Toast.LENGTH_SHORT).show();
                                setDoneCheckBox.setChecked(false);
                            }
                        } else {
                            if (pendingSets.containsKey(exercise.getId())) {
                                List<PendingSet> sets = pendingSets.get(exercise.getId());
                                sets.removeIf(set -> set.setNumber == finalSetNumber);
                            }

                            // Disable all subsequent sets
                            for (int j = finalSetNumber; j < checkBoxes.size(); j++) {
                                checkBoxes.get(j).setEnabled(false);
                                weightEdits.get(j).setEnabled(false);
                                repsEdits.get(j).setEnabled(false);
                            }

                            weightEdit.setEnabled(true);
                            repsEdit.setEnabled(true);
                        }
                    });

                    setsContainer.addView(setView);
                }

                // Add "Add Set" button
                Button addSetButton = new Button(requireContext());
                addSetButton.setText("+ Add Set");
                addSetButton.setOnClickListener(v -> {
                    int newSetNumber = checkBoxes.size() + 1;
                    
                    View setView = LayoutInflater.from(requireContext()).inflate(R.layout.item_set, setsContainer, false);
                    TextView setNumberTextView = setView.findViewById(R.id.setNumberTextView);
                    TextView previousSetTextView = setView.findViewById(R.id.previousSetTextView);
                    EditText weightEdit = setView.findViewById(R.id.weightEdit);
                    EditText repsEdit = setView.findViewById(R.id.repsEdit);
                    CheckBox setDoneCheckBox = setView.findViewById(R.id.setDoneCheckBox);

                    setNumberTextView.setText(String.valueOf(newSetNumber));
                    previousSetTextView.setText("0 kg x 0");

                    // For new sets, use the values from the last set if available
                     if (!previousSets.isEmpty()) {
                         ExerciseSet lastSet = previousSets.get(previousSets.size() - 1);
                         weightEdit.setText(String.format(Locale.getDefault(), "%.1f", lastSet.getWeight()));
                         repsEdit.setText(String.valueOf(lastSet.getReps()));
                     }
//                     else {
//                         weightEdit.setText("0");
//                         repsEdit.setText("0");
//                     }

                    weightEdits.add(weightEdit);
                    repsEdits.add(repsEdit);
                    checkBoxes.add(setDoneCheckBox);

                    // Enable only if previous set is completed
                    boolean previousSetDone = newSetNumber == 1 || 
                        (newSetNumber > 1 && checkBoxes.get(newSetNumber - 2).isChecked());
                    setDoneCheckBox.setEnabled(previousSetDone);
                    weightEdit.setEnabled(previousSetDone);
                    repsEdit.setEnabled(previousSetDone);

                    setDoneCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            String weightStr = weightEdit.getText().toString();
                            String repsStr = repsEdit.getText().toString();
                            
                            if (!weightStr.isEmpty() && !repsStr.isEmpty()) {
                                float weight = Float.parseFloat(weightStr);
                                int reps = Integer.parseInt(repsStr);
                                
                                if (!pendingSets.containsKey(exercise.getId())) {
                                    pendingSets.put(exercise.getId(), new ArrayList<>());
                                }
                                pendingSets.get(exercise.getId()).add(
                                    new PendingSet(exercise.getId(), newSetNumber, weight, reps)
                                );

                                weightEdit.setEnabled(false);
                                repsEdit.setEnabled(false);
                            } else {
                                Toast.makeText(requireContext(), "Please enter weight and reps", Toast.LENGTH_SHORT).show();
                                setDoneCheckBox.setChecked(false);
                            }
                        } else {
                            if (pendingSets.containsKey(exercise.getId())) {
                                List<PendingSet> sets = pendingSets.get(exercise.getId());
                                sets.removeIf(set -> set.setNumber == newSetNumber);
                            }

                            weightEdit.setEnabled(true);
                            repsEdit.setEnabled(true);
                        }
                    });

                    // Add the new set view before the "Add Set" button
                    setsContainer.addView(setView, setsContainer.getChildCount() - 1);
                });
                setsContainer.addView(addSetButton);
            }
        }
    }
}
