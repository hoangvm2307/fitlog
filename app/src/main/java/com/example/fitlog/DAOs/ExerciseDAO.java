package com.example.fitlog.DAOs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.fitlog.DatabaseHelper;
import com.example.fitlog.MainActivity;
import com.example.fitlog.model.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDAO {

    private DatabaseHelper dbHelper;

    public ExerciseDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertExercise(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long newRowId = -1;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", exercise.getUserId());
            values.put("name", exercise.getName());
            values.put("instruction", exercise.getInstruction());
            values.put("bodypart", exercise.getBodypart());
            values.put("category", exercise.getCategory());
            values.put("visibility", exercise.getVisibility());
            values.put("image_name", exercise.getImageName());

            newRowId = db.insert("exercises", null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return newRowId;
    }

    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("exercises", new String[]{"id", "user_id", "name", "instruction", "bodypart", "category", "visibility", "image_name"}, null, null, null, null, null);

        try {
            int idIndex = cursor.getColumnIndexOrThrow("id");
            int userIdIndex = cursor.getColumnIndexOrThrow("user_id");
            int nameIndex = cursor.getColumnIndexOrThrow("name");
            int instructionIndex = cursor.getColumnIndexOrThrow("instruction");
            int bodypartIndex = cursor.getColumnIndexOrThrow("bodypart");
            int categoryIndex = cursor.getColumnIndexOrThrow("category");
            int visibilityIndex = cursor.getColumnIndexOrThrow("visibility");
            int imageNameIndex = cursor.getColumnIndexOrThrow("image_name");

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIndex);
                int userId = cursor.getInt(userIdIndex);
                String name = cursor.getString(nameIndex);
                String instruction = cursor.getString(instructionIndex);
                String bodypart = cursor.getString(bodypartIndex);
                String category = cursor.getString(categoryIndex);
                String visibility = cursor.getString(visibilityIndex);
                String imageName = cursor.getString(imageNameIndex);

                Exercise exercise = new Exercise(id, userId, name, instruction, bodypart, category, visibility, imageName);
                exercises.add(exercise);
            }
        } catch (IllegalArgumentException e) {
            Log.e("ExerciseDAO", "Một hoặc nhiều cột không tồn tại trong bảng exercises", e);
        } finally {
            cursor.close();
        }
        return exercises;
    }

    public void seedExercises() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Xóa dữ liệu cũ
        db.delete("exercises", null, null);

        // Dữ liệu mẫu cho bài tập
        String[][] exerciseData = {
            {"Squat", "Legs", "Strength", "Stand with feet shoulder-width apart, lower your body as if sitting back into a chair, keeping your chest up and knees over your toes.", "squat"},
            {"Bench Press", "Chest", "Strength", "Lie on a bench, lower the bar to your chest, then push it back up to the starting position.", "bench_press"},
            {"Deadlift", "Back", "Strength", "Stand with feet hip-width apart, bend at your hips and knees to lower your hands to the bar, then lift by extending your hips and knees.", "deadlift"},
            {"Shoulder Press", "Shoulders", "Strength", "Start with the bar at shoulder level, press it overhead until your arms are fully extended.", "shoulder_press"},
            {"Pull-ups", "Back", "Strength", "Hang from a bar with palms facing away from you, pull your body up until your chin is over the bar.", "pull_ups"},
            {"Lunges", "Legs", "Strength", "Step forward with one leg, lowering your hips until both knees are bent at about 90-degree angles.", "lunges"},
            {"Dumbbell Rows", "Back", "Strength", "Bend at your hips and knees, and lift a dumbbell to the side of your chest.", "dumbbell_rows"},
            {"Plank", "Core", "Strength", "Hold a push-up position with your forearms on the ground, keeping your body in a straight line.", "plank"},
            {"Bicep Curls", "Arms", "Strength", "Hold dumbbells at your sides, curl them up towards your shoulders, then lower back down.", "bicep_curls"},
            {"Tricep Dips", "Arms", "Strength", "Using parallel bars or a bench, lower your body by bending your elbows, then push back up.", "tricep_dips"}
        };

        for (String[] exercise : exerciseData) {
            ContentValues values = new ContentValues();
            values.put("user_id", 1); // Giả sử ID người dùng là 1
            values.put("name", exercise[0]);
            values.put("bodypart", exercise[1]);
            values.put("category", exercise[2]);
            values.put("instruction", exercise[3]);
            values.put("visibility", "public");
            values.put("image_name", exercise[4]); // Thêm tên hình ảnh
            db.insert("exercises", null, values);
        }
    }

    public boolean addExercise(Exercise exercise) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", exercise.getUserId());
            values.put("name", exercise.getName());
            values.put("instruction", exercise.getInstruction());
            values.put("bodypart", exercise.getBodypart());
            values.put("category", exercise.getCategory());
            values.put("visibility", exercise.getVisibility());
            values.put("image_name", exercise.getImageName()); // Thêm dòng này

            long result = db.insert("exercises", null, values);
            if (result != -1) {
                db.setTransactionSuccessful();
                return true;
            }
        } finally {
            db.endTransaction();
        }
        return false;
    }

    // Các phương thức khác giữ nguyên
}