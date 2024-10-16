package com.example.fitlog.DAOs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.fitlog.DatabaseHelper;

public class UserDAO {
    private DatabaseHelper dbHelper;

    public UserDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    private SQLiteDatabase getDatabase() {
        return dbHelper.getWritableDatabase();
    }

    public Cursor getAllUsers() {
        return getDatabase().rawQuery("SELECT * FROM users", null);
    }

    public long addUser(String username, String email, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);

        return getDatabase().insert("users", null, values);
    }

    public int updateUser(int id, String username, String email, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);

        return getDatabase().update("users", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public int deleteUser(int id) {
        return getDatabase().delete("users", "id = ?", new String[]{String.valueOf(id)});
    }

    public void addSampleUsers() {
        String[][] sampleUsers = {
                {"alice", "alice@example.com", "password123"},
                {"bob", "bob@example.com", "password123"},
                {"charlie", "charlie@example.com", "password123"},
                {"david", "david@example.com", "password123"},
                {"eva", "eva@example.com", "password123"},
                {"frank", "frank@example.com", "password123"},
                {"grace", "grace@example.com", "password123"},
                {"hank", "hank@example.com", "password123"},
                {"ivy", "ivy@example.com", "password123"},
                {"john", "john@example.com", "password123"}
        };

        for (String[] user : sampleUsers) {
            addUser(user[0], user[1], user[2]);
        }
    }
}
