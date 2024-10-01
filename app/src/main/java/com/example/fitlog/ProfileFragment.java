package com.example.fitlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView userName;
    private TextView workoutCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userName = view.findViewById(R.id.userName);
        workoutCount = view.findViewById(R.id.workoutCount);

        // TODO: Fetch user data and update UI
        updateUserInfo("Khoi Minh", 5);

        return view;
    }

    private void updateUserInfo(String name, int workouts) {
        userName.setText(name);
        workoutCount.setText(workouts + " workouts");
    }
}