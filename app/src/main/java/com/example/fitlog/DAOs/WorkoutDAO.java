package com.example.fitlog.DAOs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fitlog.DatabaseHelper;
import com.example.fitlog.model.ExerciseSet;
import com.example.fitlog.model.Workout;
import com.example.fitlog.model.Template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WorkoutDAO {
    private DatabaseHelper dbHelper;
    private ExerciseSetDAO exerciseSetDAO;
    private TemplateDAO templateDAO;

    public WorkoutDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.exerciseSetDAO = new ExerciseSetDAO(dbHelper);
        this.templateDAO = new TemplateDAO(dbHelper);
    }

    public Map<String, List<Workout>> getWorkoutHistoryByMonth() {
        Map<String, List<Workout>> workoutsByMonth = new TreeMap<>(Collections.reverseOrder());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM workout_sessions ORDER BY start_time DESC";
        Cursor cursor = db.rawQuery(query, null);

        Log.d("WorkoutDAO", "Cursor count: " + cursor.getCount());

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
                    workout.setExerciseSets(exerciseSetDAO.getExerciseSetsForWorkout(id));

                    String month = new java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault()).format(new Date(startTime));
                    
                    if (!workoutsByMonth.containsKey(month)) {
                        workoutsByMonth.put(month, new ArrayList<>());
                    }
                    workoutsByMonth.get(month).add(workout);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.d("WorkoutDAO", "Total months: " + workoutsByMonth.size());
        return workoutsByMonth;
    }

    public Map<String, Integer> getWorkoutsPerWeek() {
        Map<String, Integer> workoutsPerWeek = new java.util.LinkedHashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT strftime('%W', datetime(start_time/1000, 'unixepoch')) as week, COUNT(*) as count " +
                       "FROM workout_sessions " +
                       "WHERE start_time >= datetime('now', '-8 weeks') " +
                       "GROUP BY week " +
                       "ORDER BY week DESC " +
                       "LIMIT 8";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int weekIndex = cursor.getColumnIndex("week");
                int countIndex = cursor.getColumnIndex("count");
                
                if (weekIndex != -1 && countIndex != -1) {
                    String week = "W" + cursor.getString(weekIndex);
                    int count = cursor.getInt(countIndex);
                    workoutsPerWeek.put(week, count);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return workoutsPerWeek;
    }

    public Map<String, Integer> getWorkoutCountByWeek() {
        Map<String, Integer> workoutsByWeek = new TreeMap<>(Collections.reverseOrder());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM workout_sessions ORDER BY start_time DESC";
        Cursor cursor = db.rawQuery(query, null);

        Log.d("WorkoutDAO", "Cursor count: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                int startTimeIndex = cursor.getColumnIndex("start_time");

                if (startTimeIndex != -1) {
                    long startTime = cursor.getLong(startTimeIndex);
                    Date startDate = new Date(startTime);

                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.setTime(startDate);

                    int weekOfYear = calendar.get(java.util.Calendar.WEEK_OF_YEAR);
                    int year = calendar.get(java.util.Calendar.YEAR);

                    calendar.set(java.util.Calendar.WEEK_OF_YEAR, weekOfYear);
                    calendar.set(java.util.Calendar.YEAR, year);
                    calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault());
                    String startOfWeek = dateFormat.format(calendar.getTime());

                    String weekKey = startOfWeek;

                    if (!workoutsByWeek.containsKey(weekKey)) {
                        workoutsByWeek.put(weekKey, 0);
                    }

                    workoutsByWeek.put(weekKey, workoutsByWeek.get(weekKey) + 1);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        Log.d("WorkoutDAO", "Total weeks: " + workoutsByWeek.size());
        return workoutsByWeek;
    }

    public Workout getWorkoutById(int workoutId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Workout workout = null;

        // Query to get workout session details
        String query = "SELECT * FROM workout_sessions WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(workoutId)});

        if (cursor.moveToFirst()) {
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

                // Create new Workout object
                workout = new Workout(id, userId, templateId, new Date(startTime), new Date(endTime));

                // Get associated exercise sets
                workout.setExerciseSets(exerciseSetDAO.getExerciseSetsForWorkout(id));
            }
        }
        cursor.close();

        if (workout == null) {
            Log.e("WorkoutDAO", "No workout found for ID: " + workoutId);
        }

        return workout;
    }

    public String getWorkoutTemplateName(int templateId) {
        Template template = templateDAO.getTemplateById(templateId);
        return template != null ? template.getTitle() : "Unknown Workout";
    }

    public long insertWorkout(Workout workout) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", workout.getUserId());
        values.put("template_id", workout.getTemplateId());
        values.put("start_time", workout.getStartTime().getTime());
        return db.insert("workout_sessions", null, values);  // Changed from "workouts" to "workout_sessions"
    }

    public void updateWorkout(Workout workout) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("end_time", workout.getEndTime().getTime());
        db.update("workout_sessions", values, "id = ?", new String[]{String.valueOf(workout.getId())});  // Changed from "workouts" to "workout_sessions"
    }

    public void insertSet(ExerciseSet set) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("workout_id", set.sessionId);
        values.put("exercise_id", set.getExerciseId());
        values.put("set_number", set.getSetNumber());
        values.put("weight", set.getWeight());
        values.put("reps", set.getReps());
        db.insert("exercise_sets", null, values);
    }

    public List<ExerciseSet> getLastWorkoutSets(int exerciseId) {
        List<ExerciseSet> sets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT es.* FROM exercise_sets es " +
                       "JOIN workout_sessions ws ON es.session_id = ws.id " +  // Changed workout_id to session_id
                       "WHERE es.exercise_id = ? " +
                       "ORDER BY ws.start_time DESC, es.set_number ASC " +
                       "LIMIT 3"; // Adjust the limit as needed

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(exerciseId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int sessionId = cursor.getInt(cursor.getColumnIndex("session_id")); // Changed from workout_id
                int exerciseIdFromDb = cursor.getInt(cursor.getColumnIndex("exercise_id"));
                int setNumber = cursor.getInt(cursor.getColumnIndex("set_number"));
                float weight = cursor.getFloat(cursor.getColumnIndex("weight"));
                int reps = cursor.getInt(cursor.getColumnIndex("reps"));

                ExerciseSet set = new ExerciseSet(id, sessionId, exerciseIdFromDb, setNumber, weight, reps);
                sets.add(set);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return sets;
    }

    public long insertExerciseSet(ExerciseSet set) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("session_id", set.sessionId);  // Changed from workout_id to session_id
        values.put("exercise_id", set.getExerciseId());
        values.put("set_number", set.getSetNumber());
        values.put("weight", set.getWeight());
        values.put("reps", set.getReps());
        return db.insert("exercise_sets", null, values);
    }
}
