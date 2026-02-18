package com.example.project.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UserDB extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "User_DB";

    public static final String TABLE_NAME = "users";
    public static final String COL_ID = "id";
    public static final String EMAIL = "email";

    public UserDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                EMAIL + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean saveEmail(String email) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, 1);
        cv.put(EMAIL, email);

        long count = db.insertWithOnConflict(
                TABLE_NAME,
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        return count > 0;
    }

    public String getEmail() {
        String email = null;

        Cursor c = db.rawQuery(
                "SELECT " + EMAIL + " FROM " + TABLE_NAME + " WHERE " + COL_ID + " = 1 LIMIT 1",
                null
        );

        if (c.moveToFirst()) {
            email = c.getString(0);
        }

        c.close();
        return email;
    }

    public boolean clearSession() {
        int count = db.delete(TABLE_NAME, COL_ID + " = ?", new String[]{"1"});
        return count > 0;
    }

    public void closeDB() {
        if (db != null && db.isOpen()) db.close();
    }
}