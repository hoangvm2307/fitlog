package com.example.fitlog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.fitlog.DAOs.UserDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {
    private DatabaseHelper dbHelper;
    private UserDAO userDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            DatabaseHelper.getInstance(this).getWritableDatabase();
            // Cơ sở dữ liệu đã được tạo thành công
            Toast.makeText(this, "Cơ sở dữ liệu đã được khởi tạo", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            Toast.makeText(this, "Lỗi khi khởi tạo cơ sở dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
       
        DatabaseManager.getInstance(this).open();
        dbHelper = DatabaseHelper.getInstance(this);
        userDAO = new UserDAO(dbHelper);

        // Set StartWorkoutSession as the default screen
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new StartWorkoutSession())
                .commit();
        }

        // Set the Start Workout item as the default selected item
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_start_workout);
    }
    private boolean isDatabaseEmpty() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM users", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseManager.getInstance(this).close();
    }

    public void navigateToWorkoutDetail(int workoutId) {
        Fragment fragment = WorkoutDetailFragment.newInstance(workoutId);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
