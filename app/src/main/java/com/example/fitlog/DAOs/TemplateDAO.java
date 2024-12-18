package com.example.fitlog.DAOs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.fitlog.DatabaseHelper;
import com.example.fitlog.model.Exercise;
import com.example.fitlog.model.Template;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TemplateDAO {
    private DatabaseHelper dbHelper;
    private static final String TAG = "TemplateDAO";

    public TemplateDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insertTemplate(Template template) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", template.getUserId());
        values.put("name", template.getTitle());
        values.put("description", template.getDescription());
        values.put("visibility", template.getVisibility());
        
        if (template.getLastUsed() != null) {
            values.put("last_used", template.getLastUsed().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        return db.insert("workout_templates", null, values);
    }

    public List<Template> getTemplatesForUser(int userId) {
        List<Template> templates = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("workout_templates", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                templates.add(cursorToTemplate(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return templates;
    }

    public Template getTemplateById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("workout_templates", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);

        Template template = null;
        if (cursor.moveToFirst()) {
            String lastUsedStr = cursor.getString(cursor.getColumnIndex("last_used"));
            LocalDateTime lastUsed = null;
            if (lastUsedStr != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                lastUsed = LocalDateTime.parse(lastUsedStr, formatter);
            }
            template = cursorToTemplate(cursor);
            template.setLastUsed(lastUsed);
        }
        cursor.close();
        return template;
    }

    public int updateTemplateLastUsed(int templateId, LocalDateTime lastUsed) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        values.put("last_used", lastUsed.format(formatter));
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(templateId)};
        return db.update("workout_templates", values, whereClause, whereArgs);
    }

    public List<Template> getTemplatesByUserId(int userId) {
        List<Template> templates = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT * FROM workout_templates WHERE user_id = ? ORDER BY COALESCE(last_used, created_at) DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Template template = cursorToTemplate(cursor);
                templates.add(template);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        return templates;
    }

    public void insertExampleTemplatesIfNotExist() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String[] exampleTemplates = {
            "Full Body Workout|A comprehensive workout targeting all major muscle groups|public",
            "Upper Body Focus|Concentrate on chest, back, shoulders, and arms|public",
            "Lower Body Blast|Intense leg and glute workout|public",
            "Core Crusher|Strengthen your abs and improve stability|public"
        };

        for (String templateInfo : exampleTemplates) {
            String[] parts = templateInfo.split("\\|");
            ContentValues values = new ContentValues();
            values.put("user_id", 0); // Use 0 for example templates
            values.put("name", parts[0]);
            values.put("description", parts[1]);
            values.put("visibility", parts[2]);
            values.put("created_at", now.format(formatter));
            values.put("last_used", now.format(formatter));

            // Check if the template already exists
            Cursor cursor = db.query("workout_templates", new String[]{"id"},
                    "user_id = ? AND name = ?", new String[]{"0", parts[0]},
                    null, null, null);

            if (cursor.getCount() == 0) {
                long newRowId = db.insert("workout_templates", null, values);
                if (newRowId == -1) {
                    Log.e(TAG, "Failed to insert example template: " + parts[0]);
                } else {
                    Log.d(TAG, "Inserted example template: " + parts[0]);
                }
            }
            cursor.close();
        }
    }

    public long insertTemplates(Template template, List<Exercise> selectedExercises) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        long newRowId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", template.getUserId());
            values.put("name", template.getTitle());
            values.put("description", template.getDescription());
            values.put("visibility", template.getVisibility());
            values.put("created_at", now.format(formatter));
            values.put("last_used", now.format(formatter));

            // Check if the template already exists
            Cursor cursor = db.query("workout_templates", new String[]{"id"},
                    "user_id = ? AND name = ?",
                    new String[]{String.valueOf(template.getUserId()), template.getTitle()},
                    null, null, null);

            if (cursor.getCount() == 0) {
                newRowId = db.insert("workout_templates", null, values);
                if (newRowId != -1) {
                    // Insert exercises into template_exercises table
                    if(!selectedExercises.isEmpty()){
                        for (int i = 0; i < selectedExercises.size(); i++) {
                            ContentValues exerciseValues = new ContentValues();
                            exerciseValues.put("template_id", newRowId);
                            exerciseValues.put("exercise_id", selectedExercises.get(i).getId());
                            exerciseValues.put("exercise_order", i + 1);

                            long result = db.insert("template_exercises", null, exerciseValues);
                            if (result == -1) {
                                throw new Exception("Failed to insert exercise for template");
                            }
                        }
                    }

                    Log.d(TAG, "Inserted template: " + template.getTitle());
                    db.setTransactionSuccessful();
                } else {
                    Log.e(TAG, "Failed to insert template: " + template.getTitle());
                }
            } else {
                Log.d(TAG, "Template already exists: " + template.getTitle());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error in insertTemplates: " + e.getMessage());
            newRowId = -1;
        } finally {
            db.endTransaction();
        }

        return newRowId;
    }

    public boolean updateTemplate(Template template, List<Exercise> selectedExercises) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        boolean isSuccess = false;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("name", template.getTitle());
            values.put("description", template.getDescription());
            values.put("visibility", template.getVisibility());
            values.put("last_used", now.format(formatter));

            // Update the template
            int updatedRows = db.update("workout_templates",
                    values,
                    "id = ? AND user_id = ?",
                    new String[]{String.valueOf(template.getId()), String.valueOf(template.getUserId())}
            );

            if (updatedRows > 0) {
                // Delete existing exercises for this template
                db.delete("template_exercises",
                        "template_id = ?",
                        new String[]{String.valueOf(template.getId())}
                );

                // Insert new exercises
                if (!selectedExercises.isEmpty()) {
                    for (int i = 0; i < selectedExercises.size(); i++) {
                        ContentValues exerciseValues = new ContentValues();
                        exerciseValues.put("template_id", template.getId());
                        exerciseValues.put("exercise_id", selectedExercises.get(i).getId());
                        exerciseValues.put("exercise_order", i + 1);

                        long result = db.insert("template_exercises", null, exerciseValues);
                        if (result == -1) {
                            throw new Exception("Failed to insert exercise for template");
                        }
                    }
                }

                Log.d(TAG, "Updated template: " + template.getTitle());
                db.setTransactionSuccessful();
                isSuccess = true;
            } else {
                Log.e(TAG, "Failed to update template: " + template.getTitle());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in updateTemplate: " + e.getMessage());
            isSuccess = false;
        } finally {
            db.endTransaction();
        }

        return isSuccess;
    }

    public boolean deleteTemplate(long templateId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean isSuccess = false;

        db.beginTransaction();
        try {
            // First delete related exercises in template_exercises table
            int deletedExercises = db.delete("template_exercises",
                    "template_id = ?",
                    new String[]{String.valueOf(templateId)}
            );
            Log.d(TAG, "Deleted " + deletedExercises + " exercises from template");

            // Then delete the template
            int deletedRows = db.delete("workout_templates",
                    "id = ?",
                    new String[]{String.valueOf(templateId)}
            );

            if (deletedRows > 0) {
                Log.d(TAG, "Deleted template with ID: " + templateId);
                db.setTransactionSuccessful();
                isSuccess = true;
            } else {
                Log.e(TAG, "Failed to delete template with ID: " + templateId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in deleteTemplate: " + e.getMessage());
            isSuccess = false;
        } finally {
            db.endTransaction();
        }

        return isSuccess;
    }

    public List<Template> getExampleTemplates() {
        List<Template> templates = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        String query = "SELECT * FROM workout_templates WHERE user_id = 0 ORDER BY created_at DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Template template = cursorToTemplate(cursor);
                templates.add(template);
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        return templates;
    }

    private Template cursorToTemplate(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        String visibility = cursor.getString(cursor.getColumnIndexOrThrow("visibility"));
        String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime createdAt = LocalDateTime.parse(createdAtString, formatter);
        LocalDateTime lastUsed = null;

        int lastUsedColumnIndex = cursor.getColumnIndex("last_used");
        if (lastUsedColumnIndex != -1) {
            String lastUsedString = cursor.getString(lastUsedColumnIndex);
            if (lastUsedString != null && !lastUsedString.isEmpty()) {
                lastUsed = LocalDateTime.parse(lastUsedString, formatter);
            }
        }

        return new Template(id, userId, name, description, visibility, createdAt, lastUsed);
    }

    public void updateTemplateLastUsed(int templateId, Date lastUsed) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        // Format the date as "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(lastUsed);
        
        values.put("last_used", formattedDate);
        
        db.update("template_exercises", values, "id = ?", new String[]{String.valueOf(templateId)});
    }

    // Add more methods as needed (update, delete, etc.)
}
