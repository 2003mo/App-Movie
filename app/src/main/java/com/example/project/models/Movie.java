package com.example.project.models;

import java.util.ArrayList;

public class Movie {

    public int id;
    public int posterRes;
    public String title;
    public String rating;
    public String posterUrl;
    public String summary;
    public String duration;
    public ArrayList<String> genres;
    public static final String TABLE_NAME = "favorites";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String RATING = "rating";
    public static final String POSTER_RES = "posterRes";
    public static final String POSTER_URL = "posterUrl";

    public static final String CREATE_TABLE ="CREATE TABLE " + TABLE_NAME +
            "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT NOT NULL UNIQUE, " +
            RATING + " TEXT, " +
            POSTER_RES + " INTEGER, " +
            POSTER_URL + " TEXT" + ")";


    public Movie() {}
    public Movie(int posterRes, String title, String rating) {
        this.posterRes = posterRes;
        this.title = title;
        this.rating = rating;
    }
    public Movie(int id, int posterRes, String title, String rating, String posterUrl) {
        this.id = id;
        this.posterRes = posterRes;
        this.title = title;
        this.rating = rating;
        this.posterUrl = posterUrl;

    }
}