package com.example.fitlog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "WorkoutApp.db";
    private static final int DATABASE_VERSION = 1;

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
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "name VARCHAR," +
                    "instruction TEXT," +
                    "bodypart VARCHAR," +
                    "category VARCHAR," +
                    "visibility VARCHAR," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";

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
        // Xử lý nâng cấp cơ sở dữ liệu ở đây nếu cần
        // Ví dụ: db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // onCreate(db);
    }
}
