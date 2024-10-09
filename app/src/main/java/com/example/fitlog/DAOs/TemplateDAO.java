package com.example.fitlog.DAOs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.fitlog.DatabaseHelper;
import com.example.fitlog.model.Template;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TemplateDAO {
    private DatabaseHelper dbHelper;

    public TemplateDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertTemplate(Template template) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long newRowId = -1;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("id", template.getId());
            values.put("user_id", template.getUserId());
            values.put("name", template.getName());
            values.put("description", template.getDescription());
            values.put("visibility", template.getVisibility());
            values.put("createAt", template.getCreateAt());
            values.put("lastUsed", template.getLastUsed());

            newRowId = db.insert("templates", null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return newRowId;
    }

    public List<Template> getAllTemplates() {
        List<Template> templates = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("templates", new String[]{"id", "user_id", "name", "description", "visibility", "createAt", "lastUsed"}, null, null, null, null, null);

        try {
            int idIndex = cursor.getColumnIndexOrThrow("id");
            int userIdIndex = cursor.getColumnIndexOrThrow("user_id");
            int nameIndex = cursor.getColumnIndexOrThrow("name");
            int descriptionIndex = cursor.getColumnIndexOrThrow("description");
            int visibilityIndex = cursor.getColumnIndexOrThrow("visibility");
            int createAtIndex = cursor.getColumnIndexOrThrow("createAt");
            int lastUsedIndex = cursor.getColumnIndexOrThrow("lastUsed");

            while (cursor.moveToNext()) {
                int id = cursor.getInt(idIndex);
                int userId = cursor.getInt(userIdIndex);
                String name = cursor.getString(nameIndex);
                String description = cursor.getString(descriptionIndex);
                String visibility = cursor.getString(visibilityIndex);
                LocalDateTime createAt = LocalDateTime.parse(cursor.getString(createAtIndex));
                LocalDateTime lastUsed = LocalDateTime.parse(cursor.getString(lastUsedIndex));

                Template template = new Template(id, userId, name, description, visibility, createAt, lastUsed);
                templates.add(template);
            }
        } catch (IllegalArgumentException e) {
            Log.e("TemplateDAO", "Một hoặc nhiều cột không tồn tại trong bảng template", e);
        } finally {
            cursor.close();
        }
        return templates;
    }

    public void seedTemplate() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Xóa dữ liệu cũ
        db.delete("template", null, null);

        // Dữ liệu mẫu cho template
        String[][] tewmplateData = {
            {"1", "Upper Body Workout", "Chest, shoulders, and arms templates", "private", LocalDateTime.now().toString(), LocalDateTime.now().minusDays(3).toString()},
            {"2", "Lower Body Workout", "Legs and glutes templates", "private", LocalDateTime.now().toString(), LocalDateTime.now().minusDays(3).toString()},
            {"1", "Upper Body Workout", "Chest, shoulders, and arms templates", "private", LocalDateTime.now().toString(), LocalDateTime.now().minusDays(3).toString()},
            {"2", "Lower Body Workout", "Legs and glutes templates", "private", LocalDateTime.now().toString(), LocalDateTime.now().minusDays(3).toString()},
        };

        for (String[] template : tewmplateData) {
            ContentValues values = new ContentValues();
            values.put("user_id", template[0]); // Giả sử ID người dùng là 1
            values.put("name", template[1]);
            values.put("description", template[2]);
            values.put("visibility", template[3]);
            values.put("createAt", template[4]);
            values.put("lastUsed", template[5]);
            db.insert("template", null, values);
        }
    }

    public boolean addTemplate(Template template) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("user_id", template.getUserId());
            values.put("name", template.getName());
            values.put("description", template.getDescription());
            values.put("visibility", template.getVisibility());
            values.put("createAt", template.getCreateAt());
            values.put("lastUsed", template.getLastUsed());

            long result = db.insert("template", null, values);
            if (result != -1) {
                db.setTransactionSuccessful();
                return true;
            }
        } finally {
            db.endTransaction();
        }
        return false;
    }
}
