package com.example.memyself.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.memyself.popularmovies.BuildConfig;
import com.example.memyself.popularmovies.R;
import com.example.memyself.popularmovies.data.MoviesContract;

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

//The implementation here is almost the same as that of the syncadapter used in the Sunshine app of the
//Developing Android Apps course. It fetches popular and top_rated movies related data and inserts them
//to their respective tables in the database. Once activated, this syncadapter updates the database every
//10 hours.
//The sunshine syncadapter can be found here:
//https://github.com/udacity/Sunshine-Version-2/blob/sunshine_master/app/src/main/java/com/example/android/sunshine/app/sync/SunshineSyncAdapter.java
public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter{
    public final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 60 * 10;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 10;

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult){
        fetchMovieDataFromApi("popular");
        fetchMovieDataFromApi("top_rated");
    }

    private void fetchMovieDataFromApi(String sortType){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        String apiKey = BuildConfig.MOVIE_DB_API_KEY;
        try{
            final String baseUrl = "http://api.themoviedb.org/3/movie/";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri = Uri.parse(baseUrl).buildUpon()
                    .appendPath(sortType)
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
            moviesJsonStr = buffer.toString();
            getMoviesDataFromJson(moviesJsonStr, sortType);
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

    private void getMoviesDataFromJson(String moviesJsonStr, String sortType) throws JSONException{
        try {
            JSONObject jsonObject = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = jsonObject.getJSONArray("results");

            Vector<ContentValues> cvVector = new Vector<ContentValues>(moviesArray.length());

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieInfo = moviesArray.getJSONObject(i);

                String title = movieInfo.getString("original_title");
                String posterPath = movieInfo.getString("poster_path");
                String overview = movieInfo.getString("overview");
                int movieId = movieInfo.getInt("id");
                double voteAverage = movieInfo.getDouble("vote_average");
                String releaseDate = movieInfo.getString("release_date");

                ContentValues moviesData = new ContentValues();
                moviesData.put(MoviesContract.PopularEntry.COLUMN_ORIGINAL_TITLE, title);
                moviesData.put(MoviesContract.PopularEntry.COLUMN_POSTER_PATH, posterPath);
                moviesData.put(MoviesContract.PopularEntry.COLUMN_OVERVIEW, overview);
                moviesData.put(MoviesContract.PopularEntry.COLUMN_MOVIE_ID, movieId);
                moviesData.put(MoviesContract.PopularEntry.COLUMN_VOTE_AVERAGE, voteAverage);
                moviesData.put(MoviesContract.PopularEntry.COLUMN_RELEASE_DATE, releaseDate);

                cvVector.add(moviesData);
            }
            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                if (sortType.equals("popular")) {
                    Cursor movieCursor = getContext().getContentResolver().query(MoviesContract.PopularEntry.CONTENT_URI,
                            new String[]{MoviesContract.PopularEntry._ID, MoviesContract.PopularEntry.COLUMN_ORIGINAL_TITLE},
                            null, null, null);
                    if (movieCursor.getCount() > 0) {
                        getContext().getContentResolver().delete(MoviesContract.PopularEntry.CONTENT_URI, null, null);
                        getContext().getContentResolver().bulkInsert(MoviesContract.PopularEntry.CONTENT_URI, cvArray);
                    } else {
                        getContext().getContentResolver().bulkInsert(MoviesContract.PopularEntry.CONTENT_URI, cvArray);
                    }
                    movieCursor.close();
                } else if (sortType.equals("top_rated")){
                    Cursor movieCursor = getContext().getContentResolver().query(MoviesContract.TopRatedEntry.CONTENT_URI,
                            new String[]{MoviesContract.TopRatedEntry._ID, MoviesContract.TopRatedEntry.COLUMN_ORIGINAL_TITLE},
                            null, null, null);
                    if (movieCursor.getCount() > 0) {
                        getContext().getContentResolver().delete(MoviesContract.TopRatedEntry.CONTENT_URI, null, null);
                        getContext().getContentResolver().bulkInsert(MoviesContract.TopRatedEntry.CONTENT_URI, cvArray);
                    } else {
                        getContext().getContentResolver().bulkInsert(MoviesContract.TopRatedEntry.CONTENT_URI, cvArray);
                    }
                    movieCursor.close();
                }
            }
        }catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopularMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
