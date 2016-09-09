package com.example.memyself.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.memyself.popularmovies.sync.PopularMoviesSyncAdapter;

//The MainActivity implementation is very similar to the MainActivity implementation in Sunshine app of
//Developing Android Apps course. Thus, the underlying logic of that implementation also applies here. Specifically,
//the way the app alternates between 1 pane and 2 pane mode depending on the screen size.
//The sunshine MainActivity implementation can be found here:
//https://github.com/udacity/Sunshine-Version-2/blob/sunshine_master/app/src/main/java/com/example/android/sunshine/app/MainActivity.java
public class MainActivity extends AppCompatActivity implements MainFragment.Callback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String sort_type;
    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sort_type = sharedPref.getString(getString(R.string.sort_type), getString(R.string.default_sort_val));

        if (findViewById(R.id.movie_detail_container) != null){
            mTwoPane = true;

            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else{
            mTwoPane = false;
        }

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        mainFragment.setUseThreeColGridView(mTwoPane);

        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sortType = sharedPref.getString(getString(R.string.sort_type), getString(R.string.default_sort_val));
        if (!sortType.equals(sort_type)){
            MainFragment mf = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if (mf != null){
                mf.onSortOrderChanged();
            }
            sort_type = sortType;

        }
        Log.d(LOG_TAG, "Loader should restart now");
    }

    @Override
    public void onItemSelected(Uri contentUri){
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        else{
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
