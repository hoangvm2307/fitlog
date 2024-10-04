package com.example.fitlog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.example.fitlog.model.Workout;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.ExerciseSet;
import android.content.ContentValues;
import android.util.Log;

import java.util.Random;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "WorkoutApp.db";
    private static final int DATABASE_VERSION = 2; // Tăng từ 1 lên 2

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }
    // Câu lệnh SQL để tạo bảng users
    private static final String SQL_CREATE_USERS =
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username VARCHAR," +
                    "email VARCHAR," +
                    "password VARCHAR," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

    // Câu lệnh SQL để tạo bảng workout_templates
    private static final String SQL_CREATE_WORKOUT_TEMPLATES =
            "CREATE TABLE workout_templates (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "name VARCHAR," +
                    "description TEXT," +
                    "visibility VARCHAR," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";

    // Câu lệnh SQL để tạo bảng exercises
    private static final String SQL_CREATE_EXERCISES =
            "CREATE TABLE exercises (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "name TEXT, " +
                    "instruction TEXT, " +
                    "bodypart TEXT, " +
                    "category TEXT, " +
                    "visibility TEXT, " +
                    "image_name TEXT)";

    // Câu lệnh SQL để tạo bảng exercise_images
    private static final String SQL_CREATE_EXERCISE_IMAGES =
            "CREATE TABLE exercise_images (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "exercise_id INTEGER," +
                    "image_url VARCHAR," +
                    "FOREIGN KEY (exercise_id) REFERENCES exercises(id))";

    // Câu lệnh SQL để tạo bảng template_exercises
    private static final String SQL_CREATE_TEMPLATE_EXERCISES =
            "CREATE TABLE template_exercises (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "template_id INTEGER," +
                    "exercise_id INTEGER," +
                    "exercise_order INTEGER," +
                    "FOREIGN KEY (template_id) REFERENCES workout_templates(id)," +
                    "FOREIGN KEY (exercise_id) REFERENCES exercises(id))";

    // Câu lệnh SQL để tạo bảng workout_sessions
    private static final String SQL_CREATE_WORKOUT_SESSIONS =
            "CREATE TABLE workout_sessions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "template_id INTEGER," +
                    "start_time TIMESTAMP," +
                    "end_time TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (template_id) REFERENCES workout_templates(id))";

    // Câu lệnh SQL để tạo bảng exercise_sets
    private static final String SQL_CREATE_EXERCISE_SETS =
            "CREATE TABLE exercise_sets (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "session_id INTEGER," +
                    "exercise_id INTEGER," +
                    "set_number INTEGER," +
                    "weight FLOAT," +
                    "reps INTEGER," +
                    "FOREIGN KEY (session_id) REFERENCES workout_sessions(id)," +
                    "FOREIGN KEY (exercise_id) REFERENCES exercises(id))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS);
        db.execSQL(SQL_CREATE_WORKOUT_TEMPLATES);
        db.execSQL(SQL_CREATE_EXERCISES);
        db.execSQL(SQL_CREATE_EXERCISE_IMAGES);
        db.execSQL(SQL_CREATE_TEMPLATE_EXERCISES);
        db.execSQL(SQL_CREATE_WORKOUT_SESSIONS);
        db.execSQL(SQL_CREATE_EXERCISE_SETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Thêm cột image_name vào bảng exercises
            db.execSQL("ALTER TABLE exercises ADD COLUMN image_name TEXT");
            
            // Cập nhật dữ liệu cho cột image_name
            updateExerciseImages(db);
        }
    }

    private void updateExerciseImages(SQLiteDatabase db) {
        String[] exerciseNames = {"Squat", "Bench Press", "Deadlift", "Shoulder Press", "Pull-ups", 
                                  "Lunges", "Dumbbell Rows", "Plank", "Bicep Curls", "Tricep Dips"};
        String[] imageNames = {"squat", "bench_press", "deadlift", "shoulder_press", "pull_ups", 
                               "lunges", "dumbbell_rows", "plank", "bicep_curls", "tricep_dips"};
        
        for (int i = 0; i < exerciseNames.length; i++) {
            ContentValues values = new ContentValues();
            values.put("image_name", imageNames[i]);
            db.update("exercises", values, "name = ?", new String[]{exerciseNames[i]});
        }
    }

    // Add this method to the DatabaseHelper class
    public Map<String, List<Workout>> getWorkoutHistoryByMonth() {
        Map<String, List<Workout>> workoutsByMonth = new TreeMap<>(Collections.reverseOrder());
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM workout_sessions ORDER BY start_time DESC";
        Cursor cursor = db.rawQuery(query, null);

        Log.d("DatabaseHelper", "Cursor count: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int userIdIndex = cursor.getColumnIndex("user_id");
                int templateIdIndex = cursor.getColumnIndex("template_id");
                int startTimeIndex = cursor.getColumnIndex("start_time");
                int endTimeIndex = cursor.getColumnIndex("end_time");

                if (idIndex != -1 && userIdIndex != -1 && templateIdIndex != -1 && 
                    startTimeIndex != -1 && endTimeIndex != -1) {
                    
                    int id = cursor.getInt(idIndex);
                    int userId = cursor.getInt(userIdIndex);
                    int templateId = cursor.getInt(templateIdIndex);
                    long startTime = cursor.getLong(startTimeIndex);
                    long endTime = cursor.getLong(endTimeIndex);

                    Workout workout = new Workout(id, userId, templateId, new Date(startTime), new Date(endTime));
                    workout.setExerciseSets(getExerciseSetsForWorkout(id));

                    String month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date(startTime));
                    
                    if (!workoutsByMonth.containsKey(month)) {
                        workoutsByMonth.put(month, new ArrayList<>());
                    }
                    workoutsByMonth.get(month).add(workout);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.d("DatabaseHelper", "Total months: " + workoutsByMonth.size());
        return workoutsByMonth;
    }

    private List<ExerciseSet> getExerciseSetsForWorkout(int workoutId) {
        List<ExerciseSet> exerciseSets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT * FROM exercise_sets WHERE session_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(workoutId)});
        
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int sessionIdIndex = cursor.getColumnIndex("session_id");
                int exerciseIdIndex = cursor.getColumnIndex("exercise_id");
                int setNumberIndex = cursor.getColumnIndex("set_number");
                int weightIndex = cursor.getColumnIndex("weight");
                int repsIndex = cursor.getColumnIndex("reps");

                if (idIndex != -1 && sessionIdIndex != -1 && exerciseIdIndex != -1 && 
                    setNumberIndex != -1 && weightIndex != -1 && repsIndex != -1) {
                    
                    int id = cursor.getInt(idIndex);
                    int sessionId = cursor.getInt(sessionIdIndex);
                    int exerciseId = cursor.getInt(exerciseIdIndex);
                    int setNumber = cursor.getInt(setNumberIndex);
                    float weight = cursor.getFloat(weightIndex);
                    int reps = cursor.getInt(repsIndex);
                    
                    ExerciseSet exerciseSet = new ExerciseSet(id, sessionId, exerciseId, setNumber, weight, reps);
                    exerciseSets.add(exerciseSet);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        return exerciseSets;
    }

    // Add this method to seed data
    public void seedData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Random random = new Random();

        // Clear existing data
        db.execSQL("DELETE FROM exercise_sets");
        db.execSQL("DELETE FROM workout_sessions");
        db.execSQL("DELETE FROM template_exercises");
        db.execSQL("DELETE FROM exercises");
        db.execSQL("DELETE FROM workout_templates");
        db.execSQL("DELETE FROM users");

        // Reset auto-increment counters
        db.execSQL("DELETE FROM sqlite_sequence WHERE name IN ('exercise_sets', 'workout_sessions', 'template_exercises', 'exercises', 'workout_templates', 'users')");

        // Seed users
        ContentValues userValues = new ContentValues();
        userValues.put("username", "john_doe");
        userValues.put("email", "john@example.com");
        userValues.put("password", "password123");
        long userId = db.insert("users", null, userValues);

        // Seed workout templates
        String[] templateNames = {"Full Body Workout", "Upper Body", "Lower Body", "Core Strength"};
        for (String templateName : templateNames) {
            ContentValues templateValues = new ContentValues();
            templateValues.put("user_id", userId);
            templateValues.put("name", templateName);
            templateValues.put("description", "A sample " + templateName + " workout");
            templateValues.put("visibility", "public");
            long templateId = db.insert("workout_templates", null, templateValues);

            // Seed exercises with image_name
            String[][] exerciseData = {
                    {"Squat", "Full Body", "Strength", "Perform the Squat with proper form", "squat"},
                    {"Bench Press", "Full Body", "Strength", "Perform the Bench Press with proper form", "bench_press"},
                    {"Deadlift", "Full Body", "Strength", "Perform the Deadlift with proper form", "deadlift"},
                    {"Shoulder Press", "Full Body", "Strength", "Perform the Shoulder Press with proper form", "shoulder_press"},
                    {"Pull-ups", "Full Body", "Strength", "Perform the Pull-ups with proper form", "pull_ups"}
            };

            for (String[] exercise : exerciseData) {
                ContentValues exerciseValues = new ContentValues();
                exerciseValues.put("user_id", userId);
                exerciseValues.put("name", exercise[0]);
                exerciseValues.put("instruction", exercise[3]);
                exerciseValues.put("bodypart", exercise[1]);
                exerciseValues.put("category", exercise[2]);
                exerciseValues.put("visibility", "public");
                exerciseValues.put("image_name", exercise[4]); // Thêm cột image_name
                long exerciseId = db.insert("exercises", null, exerciseValues);

                // Link exercise to template
                ContentValues templateExerciseValues = new ContentValues();
                templateExerciseValues.put("template_id", templateId);
                templateExerciseValues.put("exercise_id", exerciseId);
                templateExerciseValues.put("exercise_order", random.nextInt(5) + 1);
                db.insert("template_exercises", null, templateExerciseValues);
            }

            // Seed workout sessions (2-4 workouts per week for the past 8 weeks)
            long currentTime = System.currentTimeMillis();
            for (int week = 0; week < 8; week++) {
                int workoutsThisWeek = random.nextInt(3) + 2; // 2-4 workouts per week
                for (int workout = 0; workout < workoutsThisWeek; workout++) {
                    long startTime = currentTime - (week * 7 + random.nextInt(7)) * 24 * 60 * 60 * 1000L;
                    long endTime = startTime + (45 + random.nextInt(46)) * 60 * 1000L; // 45-90 minute workout

                    ContentValues sessionValues = new ContentValues();
                    sessionValues.put("user_id", userId);
                    sessionValues.put("template_id", templateId);
                    sessionValues.put("start_time", startTime);
                    sessionValues.put("end_time", endTime);
                    long sessionId = db.insert("workout_sessions", null, sessionValues);

                    // Seed exercise sets
                    for (int j = 0; j < 5; j++) {
                        ContentValues setValues = new ContentValues();
                        setValues.put("session_id", sessionId);
                        setValues.put("exercise_id", random.nextInt(5) + 1);
                        setValues.put("set_number", j + 1);
                        setValues.put("weight", (random.nextInt(20) + 1) * 5); // Weight in multiples of 5kg, from 5 to 100kg
                        int reps;
                        int repChoice = random.nextInt(10);
                        if (repChoice < 4) reps = 10;
                        else if (repChoice < 8) reps = 12;
                        else reps = random.nextInt(13) + 4; // 4-16 rep range for variety
                        setValues.put("reps", reps);
                        db.insert("exercise_sets", null, setValues);
                    }
                }
            }
        }
    }


    public String getExerciseName(int exerciseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT name FROM exercises WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(exerciseId)});
        
        String name = "Unknown Exercise";
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            if (nameIndex != -1) {
                name = cursor.getString(nameIndex);
            }
        }
        cursor.close();
        return name;
    }

    // Add this method to the DatabaseHelper class
    public String getWorkoutTemplateName(int templateId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT name FROM workout_templates WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(templateId)});
        
        String name = "Unknown Workout";
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("name");
            if (nameIndex != -1) {
                name = cursor.getString(nameIndex);
            }
        }
        cursor.close();
        return name;
    }

    // Add these methods to the DatabaseHelper class

    public Workout getWorkoutById(int workoutId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Workout workout = null;

        String query = "SELECT * FROM workout_sessions WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(workoutId)});

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndex("user_id"));
            int templateId = cursor.getInt(cursor.getColumnIndex("template_id"));
            long startTime = cursor.getLong(cursor.getColumnIndex("start_time"));
            long endTime = cursor.getLong(cursor.getColumnIndex("end_time"));

            workout = new Workout(workoutId, userId, templateId, new Date(startTime), new Date(endTime));
            workout.setExerciseSets(getExerciseSetsForWorkout(workoutId));
        }

        cursor.close();
        return workout;
    }



}