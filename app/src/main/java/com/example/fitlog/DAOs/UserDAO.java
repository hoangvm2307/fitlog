package com.example.fitlog.DAOs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fitlog.DatabaseHelper;
import com.example.fitlog.DatabaseManager;

public class UserDAO {
    private Context _context;

    public UserDAO(Context context) {
        _context = context;

    }
    private SQLiteDatabase db = DatabaseManager.getInstance(_context).getDatabase();

    // Phương thức để lấy tất cả người dùng từ bảng 'users'
    public Cursor getAllUsers() {
        return db.rawQuery("SELECT * FROM users", null);
    }

    // Phương thức để thêm người dùng mới vào bảng 'users'
    public long addUser(String username, String email, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);

        // Chèn hàng mới vào bảng 'users'
        return db.insert("users", null, values);
    }

    // Phương thức để cập nhật thông tin người dùng
    public int updateUser(int id, String username, String email, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);

        // Cập nhật hàng trong bảng 'users' với id tương ứng
        return db.update("users", values, "id = ?", new String[]{String.valueOf(id)});
    }

    // Phương thức để xóa người dùng dựa trên id
    public int deleteUser(int id) {
        // Xóa hàng trong bảng 'users' với id tương ứng
        return db.delete("users", "id = ?", new String[]{String.valueOf(id)});
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
