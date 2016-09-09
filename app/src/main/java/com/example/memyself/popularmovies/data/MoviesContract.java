package com.example.memyself.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

//The database will be made out of five tables: popular, top_rated, favorites, reviews, and trailers.
//popular, top_rated, and favorites tables have the same columns because they will represent the same
//kind data sorted according to popularity, ratings or user likability.
public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.example.memyself.popularmovies.app";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";
    public static final String PATH_FAVORITES = "favorites";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_TRAILERS = "trailers";

    public static final class PopularEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR;

        public static final String TABLE_NAME = "popular";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static Uri buildPopularMoviesIdUri(int movie_id){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movie_id)).build();
        }

        public static Uri buildPopularMoviesUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static int getPopularMoviesId(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static final class TopRatedEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH_TOP_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED;

        public static final String TABLE_NAME = "top_rated";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static Uri buildTopRatedMoviesIdUri(int movie_id){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movie_id)).build();
        }

        public static Uri buildTopRatedMoviesUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static int getTopRatedMoviesId(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static final class FavoritesEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static Uri buildFavoriteMoviesIdUri(int movie_id){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movie_id)).build();
        }

        public static Uri buildFavoriteMoviesUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static int getFavoriteMoviesId(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static final class ReviewsEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildMovieReviewsIdUri(int movie_id){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movie_id)).build();
        }

        public static Uri buildReviewRowIdUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static int getMovieReviewsId(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }

    public static final class TrailerEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TRAILER_KEY = "key";


        public static Uri buildTrailerMovieIdUri(int movie_id){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(movie_id)).build();
        }

        public static Uri buildTrailerRowsIdUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static int getMovieId(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }
}
