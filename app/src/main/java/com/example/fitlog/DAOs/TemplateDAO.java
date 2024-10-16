package com.example.fitlog.DAOs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.example.fitlog.DatabaseHelper;
import com.example.fitlog.model.Template;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
                lastUsed = LocalDateTime.parse(lastUsedStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
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

    // Add more methods as needed (update, delete, etc.)
}
