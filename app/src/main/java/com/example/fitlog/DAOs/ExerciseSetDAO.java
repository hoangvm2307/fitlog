package com.example.fitlog.DAOs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fitlog.DatabaseHelper;
import com.example.fitlog.model.ExerciseSet;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSetDAO {
    private DatabaseHelper dbHelper;
    private static final String TABLE_NAME = "exercise_sets";

    public ExerciseSetDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Create a new ExerciseSet
    public long createExerciseSet(ExerciseSet exerciseSet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("session_id", exerciseSet.getSessionId());
        values.put("exercise_id", exerciseSet.getExerciseId());
        values.put("set_number", exerciseSet.getSetNumber());
        values.put("weight", exerciseSet.getWeight());
        values.put("reps", exerciseSet.getReps());

        long id = db.insert(TABLE_NAME, null, values);
        if (id == -1) {
            Log.e("ExerciseSetDAO", "Failed to insert ExerciseSet");
        }
        return id;
    }

    // Read an ExerciseSet by ID
    public ExerciseSet getExerciseSetById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {"id", "session_id", "exercise_id", "set_number", "weight", "reps"};
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        ExerciseSet exerciseSet = null;

        if (cursor.moveToFirst()) {
            exerciseSet = new ExerciseSet(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getInt(cursor.getColumnIndex("session_id")),
                cursor.getInt(cursor.getColumnIndex("exercise_id")),
                cursor.getInt(cursor.getColumnIndex("set_number")),
                cursor.getFloat(cursor.getColumnIndex("weight")),
                cursor.getInt(cursor.getColumnIndex("reps"))
            );
        }
        cursor.close();
        return exerciseSet;
    }

    // Read all ExerciseSets for a specific workout session
    public List<ExerciseSet> getExerciseSetsForWorkout(int workoutId) {
        List<ExerciseSet> exerciseSets = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
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

    // Update an ExerciseSet
    public int updateExerciseSet(ExerciseSet exerciseSet) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("session_id", exerciseSet.getSessionId());
        values.put("exercise_id", exerciseSet.getExerciseId());
        values.put("set_number", exerciseSet.getSetNumber());
        values.put("weight", exerciseSet.getWeight());
        values.put("reps", exerciseSet.getReps());

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(exerciseSet.getId())};

        int rowsAffected = db.update(TABLE_NAME, values, whereClause, whereArgs);
        if (rowsAffected == 0) {
            Log.e("ExerciseSetDAO", "Failed to update ExerciseSet with ID: " + exerciseSet.getId());
        }
        return rowsAffected;
    }

    // Delete an ExerciseSet
    public int deleteExerciseSet(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(id)};

        int rowsAffected = db.delete(TABLE_NAME, whereClause, whereArgs);
        if (rowsAffected == 0) {
            Log.e("ExerciseSetDAO", "Failed to delete ExerciseSet with ID: " + id);
        }
        return rowsAffected;
    }
}
