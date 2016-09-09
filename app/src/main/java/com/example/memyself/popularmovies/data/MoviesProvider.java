package com.example.memyself.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by MeMyself on 8/4/2016.
 */
public class MoviesProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper moviesDbHelper;

    static final int POPULAR = 100;
    static final int POPULAR_WITH_ID = 101;

    static final int TOP_RATED = 200;
    static final int TOP_RATED_WITH_ID = 201;

    static final int FAVORITES = 300;
    static final int FAVORITES_WITH_ID = 301;

    static final int REVIEWS = 400;
    static final int REVIEWS_WITH_ID = 401;

    static final int TRAILER = 500;
    static final int TRAILER_WITH_ID = 501;

    public static final String popularMoviesIdSelection = MoviesContract.PopularEntry.TABLE_NAME +
            "." + MoviesContract.PopularEntry.COLUMN_MOVIE_ID + " = ? ";

    public static final String topRatedMoviesIdSelection = MoviesContract.TopRatedEntry.TABLE_NAME +
            "." + MoviesContract.TopRatedEntry.COLUMN_MOVIE_ID + " = ? ";

    public static final String favoriteMoviesIdSelection = MoviesContract.FavoritesEntry.TABLE_NAME +
            "." + MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ? ";

    public static final String movieReviewsIdSelection = MoviesContract.ReviewsEntry.TABLE_NAME +
            "." + MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ? ";

    public static final String movieTrailerIdSelection = MoviesContract.TrailerEntry.TABLE_NAME +
            "." + MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ";

    static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MoviesContract.PATH_POPULAR, POPULAR);
        matcher.addURI(authority, MoviesContract.PATH_POPULAR + "/#", POPULAR_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_TOP_RATED, TOP_RATED);
        matcher.addURI(authority, MoviesContract.PATH_TOP_RATED + "/#", TOP_RATED_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, MoviesContract.PATH_FAVORITES + "/#", FAVORITES_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/#", REVIEWS_WITH_ID);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS, TRAILER);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS + "/#", TRAILER_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate(){
        moviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);

        switch (match){
            case POPULAR:
                return MoviesContract.PopularEntry.CONTENT_TYPE;
            case POPULAR_WITH_ID:
                return MoviesContract.PopularEntry.CONTENT_ITEM_TYPE;
            case TOP_RATED:
                return MoviesContract.TopRatedEntry.CONTENT_TYPE;
            case TOP_RATED_WITH_ID:
                return MoviesContract.TopRatedEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MoviesContract.FavoritesEntry.CONTENT_TYPE;
            case FAVORITES_WITH_ID:
                return MoviesContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEWS_WITH_ID:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case TRAILER:
                return MoviesContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_ID:
                return MoviesContract.TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case POPULAR:
                retCursor = moviesDbHelper.getReadableDatabase()
                        .query(MoviesContract.PopularEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case POPULAR_WITH_ID:
                selection = popularMoviesIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.PopularEntry.getPopularMoviesId(uri))};
                retCursor = moviesDbHelper.getReadableDatabase()
                        .query(MoviesContract.PopularEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case TOP_RATED:
                retCursor = moviesDbHelper.getReadableDatabase()
                        .query(MoviesContract.TopRatedEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case TOP_RATED_WITH_ID:
                selection = topRatedMoviesIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.TopRatedEntry.getTopRatedMoviesId(uri))};
                retCursor = moviesDbHelper.getReadableDatabase()
                        .query(MoviesContract.TopRatedEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case FAVORITES:
                retCursor = moviesDbHelper.getReadableDatabase()
                        .query(MoviesContract.FavoritesEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case FAVORITES_WITH_ID:
                selection = favoriteMoviesIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.FavoritesEntry.getFavoriteMoviesId(uri))};
                retCursor = moviesDbHelper.getReadableDatabase()
                        .query(MoviesContract.FavoritesEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case REVIEWS_WITH_ID:
                selection = movieReviewsIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.ReviewsEntry.getMovieReviewsId(uri))};
                retCursor = moviesDbHelper.getReadableDatabase()
                        .query(MoviesContract.ReviewsEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case TRAILER_WITH_ID:
                selection = movieTrailerIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.TrailerEntry.getMovieId(uri))};
                retCursor = moviesDbHelper.getReadableDatabase()
                        .query(MoviesContract.TrailerEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values){
        Uri retUri;
        switch (sUriMatcher.match(uri)){
            case POPULAR: {
                long _id = moviesDbHelper.getWritableDatabase().insert(MoviesContract.PopularEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    retUri = MoviesContract.PopularEntry.buildPopularMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            case TOP_RATED: {
                long _id = moviesDbHelper.getWritableDatabase().insert(MoviesContract.TopRatedEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    retUri = MoviesContract.TopRatedEntry.buildTopRatedMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            case FAVORITES: {
                long _id = moviesDbHelper.getWritableDatabase().insert(MoviesContract.FavoritesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    retUri = MoviesContract.FavoritesEntry.buildFavoriteMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            case REVIEWS: {
                long _id = moviesDbHelper.getWritableDatabase().insert(MoviesContract.ReviewsEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    retUri = MoviesContract.ReviewsEntry.buildReviewRowIdUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            case TRAILER: {
                long _id = moviesDbHelper.getWritableDatabase().insert(MoviesContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    retUri = MoviesContract.TrailerEntry.buildTrailerRowsIdUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        int rowsDeleted;
        if (selection == null) selection = "1";
        switch (sUriMatcher.match(uri)){
            case POPULAR:
                rowsDeleted = moviesDbHelper.getWritableDatabase().delete(
                        MoviesContract.PopularEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            case TOP_RATED:
                rowsDeleted = moviesDbHelper.getWritableDatabase().delete(
                        MoviesContract.TopRatedEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            case FAVORITES:
                rowsDeleted = moviesDbHelper.getWritableDatabase().delete(
                        MoviesContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            case FAVORITES_WITH_ID:
                selection = favoriteMoviesIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.FavoritesEntry.getFavoriteMoviesId(uri))};
                rowsDeleted = moviesDbHelper.getWritableDatabase().delete(
                        MoviesContract.FavoritesEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            case REVIEWS_WITH_ID:
                selection = movieReviewsIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.ReviewsEntry.getMovieReviewsId(uri))};
                rowsDeleted = moviesDbHelper.getWritableDatabase().delete(
                        MoviesContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            case TRAILER_WITH_ID:
                selection = movieTrailerIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.TrailerEntry.getMovieId(uri))};
                rowsDeleted = moviesDbHelper.getWritableDatabase().delete(
                        MoviesContract.TrailerEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        int rowsUpdated;
        switch (sUriMatcher.match(uri)){
            case POPULAR:
                rowsUpdated = moviesDbHelper.getWritableDatabase().update(
                        MoviesContract.PopularEntry.TABLE_NAME, values, selection, selectionArgs
                );
                break;
            case TOP_RATED:
                rowsUpdated = moviesDbHelper.getWritableDatabase().update(
                        MoviesContract.TopRatedEntry.TABLE_NAME, values, selection, selectionArgs
                );
                break;
            case FAVORITES:
                rowsUpdated = moviesDbHelper.getWritableDatabase().update(
                        MoviesContract.FavoritesEntry.TABLE_NAME, values, selection, selectionArgs
                );
                break;
            case REVIEWS_WITH_ID:
                selection = movieReviewsIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.ReviewsEntry.getMovieReviewsId(uri))};
                rowsUpdated = moviesDbHelper.getWritableDatabase().update(
                        MoviesContract.ReviewsEntry.TABLE_NAME, values, selection, selectionArgs
                );
                break;
            case TRAILER_WITH_ID:
                selection = movieTrailerIdSelection;
                selectionArgs = new String[]{Integer.toString(MoviesContract.TrailerEntry.getMovieId(uri))};
                rowsUpdated = moviesDbHelper.getWritableDatabase().update(
                        MoviesContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values){
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case POPULAR: {
                db.beginTransaction();
                int counter = 0;
                try{
                    for (ContentValues value : values){
                        long _id = db.insert(MoviesContract.PopularEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            counter++;
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return counter;
            }
            case TOP_RATED: {
                db.beginTransaction();
                int counter = 0;
                try{
                    for (ContentValues value : values){
                        long _id = db.insert(MoviesContract.TopRatedEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            counter++;
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return counter;
            }
            case REVIEWS: {
                db.beginTransaction();
                int counter = 0;
                try{
                    for (ContentValues value : values){
                        long _id = db.insert(MoviesContract.ReviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            counter++;
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return counter;
            }
            case TRAILER: {
                db.beginTransaction();
                int counter = 0;
                try{
                    for (ContentValues value : values){
                        long _id = db.insert(MoviesContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            counter++;
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return counter;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
