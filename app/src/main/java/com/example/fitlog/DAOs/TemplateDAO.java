package com.example.fitlog.DAOs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.fitlog.DatabaseHelper;
import com.example.fitlog.model.Template;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TemplateDAO {
    private DatabaseHelper dbHelper;

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
        values.put("created_at", template.getCreateAt().toString());
        return db.insert("workout_templates", null, values);
    }

    public List<Template> getTemplatesForUser(int userId) {
        List<Template> templates = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("workout_templates", null, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Template template = new Template(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getInt(cursor.getColumnIndex("user_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getString(cursor.getColumnIndex("visibility")),
                    LocalDateTime.parse(cursor.getString(cursor.getColumnIndex("created_at")))
                );
                templates.add(template);
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
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String visibility = cursor.getString(cursor.getColumnIndexOrThrow("visibility"));
            String createdAtString = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

            // Parse the date-time string more flexibly
            LocalDateTime createdAt;
            try {
                createdAt = LocalDateTime.parse(createdAtString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e) {
                // If the above fails, try an alternative format
                createdAt = LocalDateTime.parse(createdAtString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            }

            template = new Template(id, 0, name, description, visibility, createdAt);
        }
        cursor.close();
        return template;
    }

    public int updateTemplateLastUsed(int templateId, LocalDateTime lastUsed) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_used", lastUsed.toString());
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(templateId)};
        return db.update("workout_templates", values, whereClause, whereArgs);
    }

    // Add more methods as needed (update, delete, etc.)
}
