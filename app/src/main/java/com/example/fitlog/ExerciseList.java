package com.example.fitlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ExerciseList extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_exercise_list, container, false);
        
        // Add your existing ExerciseList logic here
        // For now, we'll just set a text view as an example
        TextView textView = view.findViewById(R.id.exerciseListText);
        if (textView != null) {
            textView.setText("Exercise List Fragment");
        }
        
        return view;
    }
}