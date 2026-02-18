package com.example.project.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.project.models.Movie;

import java.util.ArrayList;

public class FavoritesDB extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Favorites_DB";

    public FavoritesDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Movie.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Movie.TABLE_NAME);
        onCreate(db);
    }

    public boolean addMovieToFav(Movie m) {
        if (m == null) return false;

        ContentValues cv = new ContentValues();
        cv.put(Movie.TITLE, m.title);
        cv.put(Movie.RATING, m.rating);
        cv.put(Movie.POSTER_RES, m.posterRes);
        cv.put(Movie.POSTER_URL, m.posterUrl);

        long r = db.insertWithOnConflict(Movie.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return r != -1;
    }

    public boolean removeFav(String title) {
        return db.delete(Movie.TABLE_NAME, Movie.TITLE + "=?", new String[]{title}) > 0;
    }

    public boolean isFav(String title) {
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + Movie.TABLE_NAME + " WHERE " + Movie.TITLE + "=? LIMIT 1",
                new String[]{title}
        );
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }

    public ArrayList<Movie> getAllFavMovies() {
        ArrayList<Movie> list = new ArrayList<>();

        Cursor c = db.rawQuery(
                "SELECT * FROM " + Movie.TABLE_NAME + " ORDER BY " + Movie.ID + " DESC",
                null
        );

        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow(Movie.ID));
            String title = c.getString(c.getColumnIndexOrThrow(Movie.TITLE));
            String rating = c.getString(c.getColumnIndexOrThrow(Movie.RATING));
            int posterRes = c.getInt(c.getColumnIndexOrThrow(Movie.POSTER_RES));
            String posterUrl = c.getString(c.getColumnIndexOrThrow(Movie.POSTER_URL));

            list.add(new Movie(id, posterRes, title, rating, posterUrl));
        }

        c.close();
        return list;
    }
}