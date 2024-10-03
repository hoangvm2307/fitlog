package com.example.fitlog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.fitlog.model.Exercise;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;

public class ExerciseDetailsDialogFragment extends DialogFragment {
    private static final String ARG_EXERCISE = "exercise";

    private Exercise exercise;

    public static ExerciseDetailsDialogFragment newInstance(Exercise exercise) {
        ExerciseDetailsDialogFragment fragment = new ExerciseDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EXERCISE, exercise);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exercise = getArguments().getParcelable(ARG_EXERCISE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_details, container, false);

        ImageView exerciseImage = view.findViewById(R.id.exerciseImage);
        TextView exerciseName = view.findViewById(R.id.exerciseName);
        TextView instructionsText = view.findViewById(R.id.instructionsText);
        TextView editButton = view.findViewById(R.id.editButton);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        if (exercise != null) {
            int resourceId = getResources().getIdentifier(
                    exercise.getImageName(), "drawable", getContext().getPackageName());
            if (resourceId != 0) {
                exerciseImage.setImageResource(resourceId);
            }
            exerciseName.setText(exercise.getName());
            instructionsText.setText(exercise.getInstruction());
        }

        editButton.setOnClickListener(v -> {
            // Xử lý sự kiện khi nhấn nút Edit
        });

        // Thiết lập ViewPager và TabLayout
        String[] tabTitles = new String[]{"About", "History", "Charts", "Records"};
        viewPager.setAdapter(new ExerciseDetailsPagerAdapter(this, exercise));
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
    }
}