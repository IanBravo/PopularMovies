package com.example.android.popularmovies.Data;

/**
 * Created by Ian Bravo on 15-Jun-17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MoviesDBHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_FAVORITE_MOVIES + "(" + MoviesContract.MovieEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POSTER_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_PLOT + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_FAVORITE_MOVIES);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + MoviesContract.MovieEntry.TABLE_FAVORITE_MOVIES + "'");
        onCreate(db);
    }
}
