package com.example.fitlog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitlog.model.Workout;

import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private RecyclerView historyRecyclerView;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = DatabaseHelper.getInstance(getContext());
        
        // Check if the database is empty before seeding

        loadWorkoutHistory();

        return view;
    }

    private boolean isDatabaseEmpty() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }

    private void loadWorkoutHistory() {
        Map<String, List<Workout>> workoutsByMonth = dbHelper.getWorkoutHistoryByMonth();
        Log.d("HistoryFragment", "Workouts loaded: " + workoutsByMonth.size());
        HistoryAdapter adapter = new HistoryAdapter(workoutsByMonth, getContext());
        historyRecyclerView.setAdapter(adapter);
    }
}