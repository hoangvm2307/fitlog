package com.example.fitlog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.fitlog.model.Exercise;

public class ExerciseDetailsPagerAdapter extends FragmentStateAdapter {
    private Exercise exercise;

    public ExerciseDetailsPagerAdapter(Fragment fragment, Exercise exercise) {
        super(fragment);
        this.exercise = exercise;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ExerciseAboutFragment.newInstance(exercise);
            case 1:
                return ExerciseHistoryFragment.newInstance(exercise);
            case 2:
                return ExerciseChartsFragment.newInstance(exercise);
            case 3:
                return ExerciseRecordsFragment.newInstance(exercise);
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}