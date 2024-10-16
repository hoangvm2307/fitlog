package com.example.fitlog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewExerciseDialogFragment extends DialogFragment {

    private EditText nameEditText;
    private Spinner categorySpinner;
    private Spinner bodyPartSpinner;
    private Button saveButton;
    private ImageButton closeButton;
    private String category;
    private String bodyPart;
    private ArrayAdapter<String> bodyPartAdapter; // Khai báo ở cấp class
    private ArrayAdapter<String> categoryAdapter;
    private OnExerciseAddedListener listener;
    private DatabaseHelper dbHelper;

    public interface OnExerciseAddedListener {
        void onExerciseAdded();
    }

    public void setOnExerciseAddedListener(OnExerciseAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(requireContext());
        // Khởi tạo adapters trong onCreate
        initializeAdapters();
    }
    private void initializeAdapters() {
        // Khởi tạo dữ liệu
        List<String> bodyPartItems = new ArrayList<>();
        bodyPartItems.add("Body part");
        bodyPartItems.addAll(Arrays.asList("Shoulder", "Tricep", "Chest"));

        List<String> categoryItems = new ArrayList<>();
        categoryItems.add("Category");
        categoryItems.addAll(Arrays.asList("Strength", "Cardio", "Flexibility"));

        // Khởi tạo adapters
        bodyPartAdapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_item, bodyPartItems) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        bodyPartAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        // Tương tự cho categoryAdapter

        // Khởi tạo adapters
        categoryAdapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_item, categoryItems) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        // Tương tự cho categoryAdapter
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_exercise, container, false);

        // Ánh xạ views
        nameEditText = view.findViewById(R.id.nameEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        bodyPartSpinner = view.findViewById(R.id.bodyPartSpinner);
        saveButton = view.findViewById(R.id.saveButton);
        closeButton = view.findViewById(R.id.closeButton);


        // Gán adapters
        bodyPartSpinner.setAdapter(bodyPartAdapter);
        categorySpinner.setAdapter(categoryAdapter);

        bodyPartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l){
                bodyPart = adapterView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l){
                category = adapterView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
        // Gán sự kiện
        saveButton.setOnClickListener(v -> saveNewExercise());
        closeButton.setOnClickListener(v -> dismiss());
        return view;
    }

    private void saveNewExercise() {
        String name = nameEditText.getText().toString();

        if (!name.isEmpty()) {
            Exercise newExercise = new Exercise(1, 1, name, " ", bodyPart, category, "", "");
            ExerciseDAO exerciseDAO = new ExerciseDAO(dbHelper);
            long result = exerciseDAO.insertExercise(newExercise);

            if (result != -1) {
                Toast.makeText(requireContext(), "Exercise added: " + newExercise.getName(), Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onExerciseAdded();
                }
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Failed to add exercise", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Please enter exercise name", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
