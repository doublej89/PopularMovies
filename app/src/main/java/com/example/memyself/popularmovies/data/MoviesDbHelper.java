package com.example.memyself.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MeMyself on 8/3/2016.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;
    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " + MoviesContract.PopularEntry.TABLE_NAME + " (" +
                MoviesContract.PopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.PopularEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.PopularEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.PopularEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.PopularEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.PopularEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                "UNIQUE (" + MoviesContract.PopularEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TOP_RATED_TABLE = "CREATE TABLE " + MoviesContract.TopRatedEntry.TABLE_NAME + " (" +
                MoviesContract.TopRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.TopRatedEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.TopRatedEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                "UNIQUE (" + MoviesContract.TopRatedEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + MoviesContract.FavoritesEntry.TABLE_NAME + " (" +
                MoviesContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MoviesContract.FavoritesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                "UNIQUE (" + MoviesContract.PopularEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewsEntry.TABLE_NAME + " (" +
                MoviesContract.ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL);";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MoviesContract.TrailerEntry.TABLE_NAME + " (" +
                MoviesContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.PopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TopRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoritesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
