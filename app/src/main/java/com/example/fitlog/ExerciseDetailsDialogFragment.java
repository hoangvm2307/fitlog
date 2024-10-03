package com.example.fitlog;

import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.fitlog.model.Exercise;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

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
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_details, container, false);

        TextView exerciseName = view.findViewById(R.id.exerciseName);
        TextView editButton = view.findViewById(R.id.editButton);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        ImageButton closeButton = view.findViewById(R.id.closeButton);

        if (exercise != null) {
            exerciseName.setText(exercise.getName());
        }

        editButton.setOnClickListener(v -> {
            // Xử lý sự kiện khi nhấn nút Edit
        });

        closeButton.setOnClickListener(v -> {
            dismiss();
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
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}