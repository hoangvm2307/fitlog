package com.example.fitlog;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.example.fitlog.DAOs.ExerciseDAO;
import com.example.fitlog.DAOs.ExerciseSetDAO;
import com.example.fitlog.DAOs.TemplateDAO;
import com.example.fitlog.DAOs.UserDAO;
import com.example.fitlog.DAOs.WorkoutDAO;
import com.example.fitlog.model.Workout;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.ExerciseSet;
import android.content.ContentValues;
import android.util.Log;

import java.util.Random;
import java.util.LinkedHashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "WorkoutApp.db";
    private static final int DATABASE_VERSION = 3; // Increment this

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
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "visibility TEXT, " +
                    "created_at TEXT, " +
                    "last_used TEXT, " +  // Add this line
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
            // Handle upgrade to version 2 if needed
        }
        if (oldVersion < 3) {
            // Add the last_used column to the workout_templates table
            db.execSQL("ALTER TABLE workout_templates ADD COLUMN last_used TEXT");
        }
        // Handle other version upgrades here
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
    public WorkoutDAO getWorkoutDAO() {
        return new WorkoutDAO(this);
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

        // Seed default templates with exercises
        String[] defaultTemplates = {
            "Full Body Workout|A comprehensive workout targeting all major muscle groups",
            "Upper Body Focus|Concentrate on chest, back, shoulders, and arms",
            "Lower Body Blast|Intense leg and glute workout",
            "Core Crusher|Strengthen your abs and improve stability"
        };

        for (String templateInfo : defaultTemplates) {
            String[] parts = templateInfo.split("\\|");
            ContentValues templateValues = new ContentValues();
            templateValues.put("user_id", 0); // 0 for default templates
            templateValues.put("name", parts[0]);
            templateValues.put("description", parts[1]);
            templateValues.put("visibility", "public");
            templateValues.put("created_at", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            long templateId = db.insert("workout_templates", null, templateValues);

            // Add exercises to this template
            String[][] exercisesForTemplate = getExercisesForTemplate(parts[0]);
            for (int i = 0; i < exercisesForTemplate.length; i++) {
                String[] exerciseData = exercisesForTemplate[i];
                ContentValues exerciseValues = new ContentValues();
                exerciseValues.put("user_id", 0); // 0 for default exercises
                exerciseValues.put("name", exerciseData[0]);
                exerciseValues.put("instruction", exerciseData[1]);
                exerciseValues.put("bodypart", exerciseData[2]);
                exerciseValues.put("category", exerciseData[3]);
                exerciseValues.put("visibility", "public");
                exerciseValues.put("image_name", exerciseData[4]);
                long exerciseId = db.insert("exercises", null, exerciseValues);

                // Link exercise to template
                ContentValues templateExerciseValues = new ContentValues();
                templateExerciseValues.put("template_id", templateId);
                templateExerciseValues.put("exercise_id", exerciseId);
                templateExerciseValues.put("exercise_order", i + 1);
                db.insert("template_exercises", null, templateExerciseValues);
            }
        }
    }

    private String[][] getExercisesForTemplate(String templateName) {
        switch (templateName) {
            case "Full Body Workout":
                return new String[][] {
                    {"Squat", "Perform squats with proper form", "Legs", "Strength", "squat"},
                    {"Bench Press", "Perform bench press with proper form", "Chest", "Strength", "bench_press"},
                    {"Deadlift", "Perform deadlifts with proper form", "Back", "Strength", "deadlift"},
                    {"Shoulder Press", "Perform shoulder press with proper form", "Shoulders", "Strength", "shoulder_press"},
                    {"Pull-ups", "Perform pull-ups with proper form", "Back", "Strength", "pull_ups"}
                };
            case "Upper Body Focus":
                return new String[][] {
                    {"Bench Press", "Perform bench press with proper form", "Chest", "Strength", "bench_press"},
                    {"Shoulder Press", "Perform shoulder press with proper form", "Shoulders", "Strength", "shoulder_press"},
                    {"Pull-ups", "Perform pull-ups with proper form", "Back", "Strength", "pull_ups"},
                    {"Bicep Curls", "Perform bicep curls with proper form", "Arms", "Isolation", "bicep_curls"},
                    {"Tricep Dips", "Perform tricep dips with proper form", "Arms", "Isolation", "tricep_dips"}
                };
            case "Lower Body Blast":
                return new String[][] {
                    {"Squat", "Perform squats with proper form", "Legs", "Strength", "squat"},
                    {"Deadlift", "Perform deadlifts with proper form", "Back", "Strength", "deadlift"},
                    {"Lunges", "Perform lunges with proper form", "Legs", "Strength", "lunges"},
                    {"Leg Press", "Perform leg press with proper form", "Legs", "Strength", "leg_press"},
                    {"Calf Raises", "Perform calf raises with proper form", "Legs", "Isolation", "calf_raises"}
                };
            case "Core Crusher":
                return new String[][] {
                    {"Plank", "Hold plank position with proper form", "Core", "Stability", "plank"},
                    {"Crunches", "Perform crunches with proper form", "Core", "Strength", "crunches"},
                    {"Russian Twists", "Perform Russian twists with proper form", "Core", "Strength", "russian_twists"},
                    {"Leg Raises", "Perform leg raises with proper form", "Core", "Strength", "leg_raises"},
                    {"Mountain Climbers", "Perform mountain climbers with proper form", "Core", "Cardio", "mountain_climbers"}
                };
            default:
                return new String[0][0];
        }
    }

    public ExerciseSetDAO getExerciseSetDAO() {
        return new ExerciseSetDAO(this);
    }

    public ExerciseDAO getExerciseDAO() {
        return new ExerciseDAO(this);
    }

    // Add other DAO getters as needed

    public TemplateDAO getTemplateDAO() {
        return new TemplateDAO(this);
    }

    public UserDAO getUserDAO() {
        return new UserDAO(this);
    }
}
