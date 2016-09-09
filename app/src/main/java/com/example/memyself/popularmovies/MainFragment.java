package com.example.memyself.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.memyself.popularmovies.data.MoviesContract;

//The basic structure of this implementation is quite similar to that of the ForecastFragment
//implementation in the Sunshine app of the Developing Android Apps course
//The implementation of ForecastFragment can be found here:
//https://github.com/udacity/Sunshine-Version-2/blob/sunshine_master/app/src/main/java/com/example/android/sunshine/app/ForecastFragment.java
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private MoviePosterAdapter posterAdapter;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int MOVIES_LOADER = 0;
    private boolean mUseThreeColGridVIew;
    private GridView mGridView;

    private static final String SELECTED_POSITION = "selected_position";

    public static final String[] MOVIE_COLUMNS = {
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

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }

    public MainFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        posterAdapter = new MoviePosterAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.activity_main_fragment, container, false);



        mGridView = (GridView) rootView.findViewById(R.id.posters_grid);
        mGridView.setAdapter(posterAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null){
                    int movie_id = cursor.getInt(INDEX_MOVIE_ID_COL);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String sortOrder = sharedPref.getString(getString(R.string.sort_type), getString(R.string.default_sort_val));

                    if (sortOrder.equals("popular")){
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.PopularEntry.buildPopularMoviesIdUri(movie_id));
                    }
                    else if (sortOrder.equals("favorites")){
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.FavoritesEntry.buildFavoriteMoviesIdUri(movie_id));
                    }
                    else {
                        ((Callback) getActivity()).
                                onItemSelected(MoviesContract.TopRatedEntry.buildTopRatedMoviesIdUri(movie_id));
                    }

                }
                mPosition = i;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The gridview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        //Depending on whether the app is being run on phone or tablet, the cursor adapter is asked to
        //use either a two column or a three column gridview.
        posterAdapter.setUseThreeColGridView(mUseThreeColGridVIew);

        return rootView;


    }

    void onSortOrderChanged(){
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to GridView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = sharedPref.getString(getString(R.string.sort_type), getString(R.string.default_sort_val));

        //Depending on the sort type set in the settings, the cursor loader will return cursor
        //from "popular", "top_rated", or "favorites" table from the associated database.
        if (sortType.equals("popular")){
            return new CursorLoader(getActivity(),
                    MoviesContract.PopularEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }
        else if (sortType.equals("top_rated")){
            return new CursorLoader(getActivity(),
                    MoviesContract.TopRatedEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }
        else if (sortType.equals("favorites")){
            return new CursorLoader(getActivity(),
                    MoviesContract.FavoritesEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        posterAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION){
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        posterAdapter.swapCursor(null);
    }

    public void setUseThreeColGridView(boolean useThreeColGridView){
        mUseThreeColGridVIew = useThreeColGridView;
        if (posterAdapter != null){
            posterAdapter.setUseThreeColGridView(mUseThreeColGridVIew);
        }
    }


}
