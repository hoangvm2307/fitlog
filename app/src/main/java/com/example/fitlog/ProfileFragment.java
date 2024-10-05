package com.example.fitlog;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView userName;
    private TextView workoutCount;
    private BarChart workoutsPerWeekChart;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userName = view.findViewById(R.id.userName);
        workoutCount = view.findViewById(R.id.workoutCount);
        workoutsPerWeekChart = view.findViewById(R.id.workoutsPerWeekChart);

        dbHelper = DatabaseHelper.getInstance(getContext());

        updateUserInfo("Khoi Minh", 5);
        setupWorkoutsPerWeekChart();

        return view;
    }

    private void updateUserInfo(String name, int workouts) {
        userName.setText(name);
        workoutCount.setText(workouts + " workouts");
    }

    private void setupWorkoutsPerWeekChart() {
        Map<String, Integer> workoutsPerWeek = dbHelper.getWorkoutCountByWeek();
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : workoutsPerWeek.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Workouts");
        dataSet.setColor(Color.parseColor("#C392FD"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        workoutsPerWeekChart.setData(barData);

        XAxis xAxis = workoutsPerWeekChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(45);

        YAxis leftAxis = workoutsPerWeekChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);

        workoutsPerWeekChart.getAxisRight().setEnabled(false);
        workoutsPerWeekChart.getDescription().setEnabled(false);
        workoutsPerWeekChart.setFitBars(true);
        workoutsPerWeekChart.animateY(1000);
        workoutsPerWeekChart.invalidate();
    }
}