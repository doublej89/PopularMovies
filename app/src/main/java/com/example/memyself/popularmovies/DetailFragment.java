package com.example.memyself.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.memyself.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

//This implementation is somewhat similar to that of DetailFragment in Sunshine app, but more extensive.
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mUri;
    static final String DETAIL_URI = "URI";
    static String mTrailerKey;

    private static final int MOVIE_DETAIL_LOADER = 0;
    private static final int MOVIE_DETAIL_TRAILER_LOADER = 1;
    private static final int MOVIE_DETAIL_REVIEWS_LOADER = 2;

    public static final String[] MOVIE_DETAIL_COLUMNS = {
            MoviesContract.PopularEntry._ID,
            MoviesContract.PopularEntry.COLUMN_ORIGINAL_TITLE,
            MoviesContract.PopularEntry.COLUMN_POSTER_PATH,
            MoviesContract.PopularEntry.COLUMN_OVERVIEW,
            MoviesContract.PopularEntry.COLUMN_MOVIE_ID,
            MoviesContract.PopularEntry.COLUMN_RELEASE_DATE,
            MoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE
    };

    static final int INDEX_ROW_ID_COL = 0;
    static final int INDEX_ORIGINAL_TITLE_COL = 1;
    static final int INDEX_POSTER_PATH_COL = 2;
    static final int INDEX_OVERVIEW_COL = 3;
    static final int INDEX_MOVIE_ID_COL = 4;
    static final int INDEX_RELEASE_DATE_COL = 5;
    static final int INDEX_VOTE_AVERAGE_COL = 6;

    public static final String[] MOVIE_TRAILER_COLUMNS = {
            MoviesContract.TrailerEntry._ID,
            MoviesContract.TrailerEntry.COLUMN_MOVIE_ID,
            MoviesContract.TrailerEntry.COLUMN_TRAILER_KEY
    };

    static final int INDEX_TRAILER_ROW_ID_COL = 0;
    static final int INDEX_TRAILER_MOVIE_ID_COL = 1;
    static final int INDEX_TRAILER_KEY_COL = 2;

    public static final String[] MOVIE_REVIEW_COLUMNS = {
            MoviesContract.ReviewsEntry._ID,
            MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID,
            MoviesContract.ReviewsEntry.COLUMN_AUTHOR,
            MoviesContract.ReviewsEntry.COLUMN_CONTENT
    };

    static final int INDEX_REVIEW_ROW_ID_COL = 0;
    static final int INDEX_REVIEW_MOVIE_ID = 1;
    static final int INDEX_AUTHOR_COL = 2;
    static final int INDEX_CONTENT_COL = 3;

    private TextView originalTitleView;
    private ImageView posterView;
    private TextView overviewView;
    private TextView voteAverageView;
    private TextView releaseDateView;
    private LinearLayout mTrailerListLayout;
    private LinearLayout mReviewListLayout;
    private Button favoritesButton;




    public DetailFragment() {
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.activity_detail_fragment, container, false);
        originalTitleView = (TextView) rootView.findViewById(R.id.original_title);
        posterView = (ImageView) rootView.findViewById(R.id.detail_poster);

        overviewView = (TextView) rootView.findViewById(R.id.plot_synopsis);

        voteAverageView = (TextView) rootView.findViewById(R.id.user_rating);
        releaseDateView = (TextView) rootView.findViewById(R.id.release_date);

        mTrailerListLayout = (LinearLayout) rootView.findViewById(R.id.trailer_list);
        mReviewListLayout = (LinearLayout) rootView.findViewById(R.id.reviews_list);

        favoritesButton = (Button) rootView.findViewById(R.id.favorite_button);

        if (mUri != null) {
            int movie_id = Integer.parseInt(mUri.getPathSegments().get(1));
            FetchTrailersAndReviewsTask fetchTrailersAndReviewsTask = new FetchTrailersAndReviewsTask();
            fetchTrailersAndReviewsTask.execute(movie_id);
        }

        return rootView;
    }

    //I'm using three cursor loaders because the details page of each movie requires data from three
    // different database tables (the table implied by mUri, the trailers table, and the reviews table)
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(MOVIE_DETAIL_TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(MOVIE_DETAIL_REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        if (mUri != null) {
            //The id is of the movie selected in the grid view in the MainFragment
            int movie_id = Integer.parseInt(mUri.getPathSegments().get(1));
            switch (id){
                case MOVIE_DETAIL_TRAILER_LOADER:
                    Uri tUri = MoviesContract.TrailerEntry.buildTrailerMovieIdUri(movie_id);
                    cursorLoader = new CursorLoader(getActivity(),
                            tUri,
                            MOVIE_TRAILER_COLUMNS,
                            null,
                            null,
                            null);
                    break;
                case MOVIE_DETAIL_REVIEWS_LOADER:
                    Uri rUri = MoviesContract.ReviewsEntry.buildMovieReviewsIdUri(movie_id);
                    cursorLoader = new CursorLoader(getActivity(),
                            rUri,
                            MOVIE_REVIEW_COLUMNS,
                            null,
                            null,
                            null);
                    break;
                default:
                    cursorLoader = new CursorLoader(getActivity(),
                            mUri,
                            MOVIE_DETAIL_COLUMNS,
                            null,
                            null,
                            null);
            }
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        int id = loader.getId();
        if (id == MOVIE_DETAIL_TRAILER_LOADER){
            //The trailers section of a movie's details page is filled with a list of all available trailers
            //clicking/tapping on the play button image of each trailer will take the user to a corresponding
            //youtube video showing that trailer
            if (data != null && data.moveToFirst()){
                do {

                    LinearLayout trailerLayout = new LinearLayout(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    trailerLayout.setLayoutParams(params);
                    trailerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    trailerLayout.setGravity(Gravity.CENTER_VERTICAL);
                    trailerLayout.setPadding(0, 10, 0, 10);

                    ImageView imageView = new ImageView(getActivity());
                    LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(childParams);
                    imageView.setImageResource(R.drawable.ic_play_movie);
                    imageView.setId(data.getPosition());

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int index = view.getId();
                            data.moveToPosition(index);
                            String trailerKey = data.getString(INDEX_TRAILER_KEY_COL);
                            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.youtube.com/watch?v=" + trailerKey));
                            youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            if (youtubeIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(youtubeIntent);
                            }
                        }
                    });
                    TextView textView = new TextView(getActivity());
                    textView.setLayoutParams(childParams);
                    String trailerText = "Trailer " + Integer.toString(data.getPosition() + 1);
                    textView.setText(trailerText);
                    textView.setPadding(20, 0, 0, 0);

                    trailerLayout.addView(imageView);
                    trailerLayout.addView(textView);
                    mTrailerListLayout.addView(trailerLayout);

                    View view = new View(getActivity());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            2);
                    p.setMargins(16, 0, 16, 0);
                    view.setBackgroundColor(getResources().getColor(R.color.gray));
                    view.setLayoutParams(p);

                    if (!(data.getPosition() == data.getCount()-1)){
                        mTrailerListLayout.addView(view);
                    }
                } while (data.moveToNext());
            }
        }
        else if (id == MOVIE_DETAIL_REVIEWS_LOADER){
            if (data != null && data.moveToFirst()){
                //The reviews section of the movie's details page is filled with any available reviews.
                do {
                    TextView textView = new TextView(getActivity());
                    LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    textView.setLayoutParams(childParams);
                    textView.setPadding(0, 10, 0, 10);
                    String reviewText = data.getString(INDEX_CONTENT_COL) + " - " + data.getString(INDEX_AUTHOR_COL);
                    textView.setText(reviewText);

                    mReviewListLayout.addView(textView);

                    View view = new View(getActivity());
                    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            2);
                    p.setMargins(16, 0, 16, 0);
                    view.setBackgroundColor(getResources().getColor(R.color.gray));
                    view.setLayoutParams(p);

                    if (!(data.getPosition() == data.getCount()-1)){
                        mReviewListLayout.addView(view);
                    }

                } while (data.moveToNext());
            }
        }
        else {
            if (data != null && data.moveToFirst()) {
                String originalTile = data.getString(INDEX_ORIGINAL_TITLE_COL);
                originalTitleView.setText(originalTile);

                String posterPath = data.getString(INDEX_POSTER_PATH_COL);

                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + posterPath.substring(1)).fit().into(posterView);

                String overview = data.getString(INDEX_OVERVIEW_COL);
                overviewView.setText(overview);

                String voteAverage = Double.toString(data.getDouble(INDEX_VOTE_AVERAGE_COL)) + "/10";
                voteAverageView.setText(voteAverage);

                String releaseDate = data.getString(INDEX_RELEASE_DATE_COL);
                releaseDateView.setText(releaseDate);

                Cursor cursor = getActivity().getContentResolver()
                        .query(MoviesContract.FavoritesEntry.buildFavoriteMoviesIdUri(data.getInt(INDEX_MOVIE_ID_COL)),
                                new String[]{MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID, MoviesContract.FavoritesEntry.COLUMN_POSTER_PATH},
                                null, null, null);

                if (cursor.moveToFirst()){
                    favoritesButton.setText(R.string.marked_as_favorite);
                    favoritesButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                cursor.close();

                favoritesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button button = (Button) view;

                        int movieId = data.getInt(INDEX_MOVIE_ID_COL);
                        double voteAvg = data.getDouble(INDEX_VOTE_AVERAGE_COL);
                        String posterPath = data.getString(INDEX_POSTER_PATH_COL);
                        String overview = data.getString(INDEX_OVERVIEW_COL);
                        String title = data.getString(INDEX_ORIGINAL_TITLE_COL);
                        String dateOfRelease = data.getString(INDEX_RELEASE_DATE_COL);

                        Cursor cursor = getActivity().getContentResolver()
                                .query(MoviesContract.FavoritesEntry.buildFavoriteMoviesIdUri(movieId),
                                new String[]{MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID, MoviesContract.FavoritesEntry.COLUMN_POSTER_PATH},
                                null, null, null);

                        if (!cursor.moveToFirst()){
                            button.setText(R.string.marked_as_favorite);
                            button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                            ContentValues values = new ContentValues();
                            values.put(MoviesContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE, title);
                            values.put(MoviesContract.FavoritesEntry.COLUMN_POSTER_PATH, posterPath);
                            values.put(MoviesContract.FavoritesEntry.COLUMN_OVERVIEW, overview);
                            values.put(MoviesContract.FavoritesEntry.COLUMN_MOVIE_ID, movieId);
                            values.put(MoviesContract.FavoritesEntry.COLUMN_VOTE_AVERAGE, voteAvg);
                            values.put(MoviesContract.FavoritesEntry.COLUMN_RELEASE_DATE, dateOfRelease);

                            getActivity().getContentResolver()
                                    .insert(MoviesContract.FavoritesEntry.CONTENT_URI, values);
                            cursor.close();
                        }
                        else {
                            button.setText(R.string.mark_as_favorite);
                            button.setBackgroundColor(getResources().getColor(R.color.blue));

                            getActivity().getContentResolver()
                                    .delete(MoviesContract.FavoritesEntry.buildFavoriteMoviesIdUri(movieId),
                                            null, null);
                            cursor.close();
                        }
                    }
                });
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    //This asynctask implementation is used to fetch trailers and reviews related data from
    //api.themoviedb.org in a background thread for the movie implied by mUri. After fetching,
    //the data is inserted in trailers and reviews tables of the associated database if not inserted already.
    public class FetchTrailersAndReviewsTask extends AsyncTask<Integer, Void, Void> {
        String LOG_TAG = FetchTrailersAndReviewsTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Integer... params){
            fetchMovieTrailersFromApi(params[0]);
            fetchMovieReviewsFromApi(params[0]);
            return null;
        }

        private void fetchMovieTrailersFromApi(int movie_id){
            String MOVIE_ID = Integer.toString(movie_id);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String trailersJsonStr = null;

            String apiKey = BuildConfig.MOVIE_DB_API_KEY;
            try{
                final String baseUrl = "http://api.themoviedb.org/3/movie/";
                final String API_KEY_PARAM = "api_key";
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendPath(MOVIE_ID)
                        .appendPath("videos")
                        .appendQueryParameter(API_KEY_PARAM, apiKey).build();
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null){
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    return;
                }
                trailersJsonStr = buffer.toString();
                getTrailersDataFromJson(trailersJsonStr, movie_id);
            } catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                return;
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.getStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    } catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing stream!", e);
                    }
                }
            }
            return;
        }

        private void getTrailersDataFromJson(String trailersJsonStr, int movie_id)throws JSONException{
            try {
                JSONObject jsonObject = new JSONObject(trailersJsonStr);
                JSONArray trailersArray = jsonObject.getJSONArray("results");

                Vector<ContentValues> trVector = new Vector<>(trailersArray.length());

                for (int i = 0; i < trailersArray.length(); i++){
                    JSONObject trailerInfo = trailersArray.getJSONObject(i);

                    String key = trailerInfo.getString("key");

                    ContentValues trailerData = new ContentValues();
                    trailerData.put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, movie_id);
                    trailerData.put(MoviesContract.TrailerEntry.COLUMN_TRAILER_KEY, key);

                    trVector.add(trailerData);
                }
                if (trVector.size() > 0) {
                    ContentValues[] trvArray = new ContentValues[trVector.size()];
                    trVector.toArray(trvArray);

                    Cursor trailerCursor = getActivity().getContentResolver().query(
                            MoviesContract.TrailerEntry.buildTrailerMovieIdUri(movie_id),
                            new String[]{MoviesContract.TrailerEntry.COLUMN_TRAILER_KEY}, null, null, null);

                    if (!trailerCursor.moveToFirst()) {
                        getActivity().getContentResolver().bulkInsert(MoviesContract.TrailerEntry.CONTENT_URI, trvArray);
                    }
                    trailerCursor.close();
                }
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

        private void fetchMovieReviewsFromApi(int movie_id){
            String MOVIE_ID = Integer.toString(movie_id);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewsJsonStr = null;

            String apiKey = BuildConfig.MOVIE_DB_API_KEY;
            try{
                final String baseUrl = "http://api.themoviedb.org/3/movie/";
                final String API_KEY_PARAM = "api_key";
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendPath(MOVIE_ID)
                        .appendPath("reviews")
                        .appendQueryParameter(API_KEY_PARAM, apiKey).build();
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null){
                    return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    return;
                }
                reviewsJsonStr = buffer.toString();
                getReviewsDataFromJson(reviewsJsonStr, movie_id);
            } catch (IOException e){
                Log.e(LOG_TAG, "Error", e);
                return;
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.getStackTrace();
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try{
                        reader.close();
                    } catch (final IOException e){
                        Log.e(LOG_TAG, "Error closing stream!", e);
                    }
                }
            }
            return;
        }

        private void getReviewsDataFromJson(String reviewsJsonStr, int movie_id) throws JSONException{
            try {
                JSONObject jsonObject = new JSONObject(reviewsJsonStr);
                JSONArray reviewsArray = jsonObject.getJSONArray("results");

                Vector<ContentValues> revVector = new Vector<>(reviewsArray.length());

                for (int i = 0; i < reviewsArray.length(); i++){
                    JSONObject reviewInfo = reviewsArray.getJSONObject(i);

                    String author = reviewInfo.getString("author");
                    String content = reviewInfo.getString("content");

                    ContentValues reviewData = new ContentValues();
                    reviewData.put(MoviesContract.ReviewsEntry.COLUMN_MOVIE_ID, movie_id);
                    reviewData.put(MoviesContract.ReviewsEntry.COLUMN_AUTHOR, author);
                    reviewData.put(MoviesContract.ReviewsEntry.COLUMN_CONTENT, content);

                    revVector.add(reviewData);
                }
                if (revVector.size() > 0) {
                    ContentValues[] rvArray = new ContentValues[revVector.size()];
                    revVector.toArray(rvArray);

                    Cursor reviewCursor = getActivity().getContentResolver().query(
                            MoviesContract.ReviewsEntry.buildMovieReviewsIdUri(movie_id),
                            new String[]{MoviesContract.ReviewsEntry.COLUMN_AUTHOR}, null, null, null);

                    if(!reviewCursor.moveToFirst()) {
                        getActivity().getContentResolver().bulkInsert(MoviesContract.ReviewsEntry.CONTENT_URI, rvArray);
                    }
                    reviewCursor.close();
                }
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }
}
