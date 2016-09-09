package com.example.memyself.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.support.v4.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


//The basic idea of this cursor adapter implementation was inspired by the implementation in the Sunshine app
//from the Developing Android Apps course. Although compared to Sunshine, this implementation is more simplified
//since each grid item consists of just one image view.
//Sunshine's ForecastAdapter implementation can be found here:
//https://github.com/udacity/Sunshine-Version-2/blob/sunshine_master/app/src/main/java/com/example/android/sunshine/app/ForecastAdapter.java
public class MoviePosterAdapter extends CursorAdapter{
    private boolean mUseThreeColGridView = false;

    public MoviePosterAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        ImageView posterView;

        //Here I create an image view whose width is either 1/2 the screen width or 1/6 the screen width
        //depending on whether the grid view is 2 columns or 3 columns.
        //The following five lines of code, ragarding the extraction of the display screen size (in pixels), was
        //taken from a stackoverflow post by some user named Josef Pfleger.
        //The post can be found here: http://stackoverflow.com/questions/1016896/get-screen-dimensions-in-pixels
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int cellWidth;
        if (mUseThreeColGridView){
            width = width / 2;
            cellWidth = width / 3;
        }
        else {
            cellWidth = width / 2;
        }
        posterView = new ImageView(context);
        posterView.setLayoutParams(new GridView.LayoutParams(cellWidth, cellWidth+100));


        return posterView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        ImageView posterView = (ImageView) view;
        posterView.setBackground(context.getResources().getDrawable(R.drawable.touch_selector));
        String posterPath = cursor.getString(MainFragment.INDEX_POSTER_PATH_COL);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + posterPath.substring(1)).fit().into(posterView);
    }

    public void setUseThreeColGridView(boolean useThreeColGridView){
        mUseThreeColGridView = useThreeColGridView;
    }

}
