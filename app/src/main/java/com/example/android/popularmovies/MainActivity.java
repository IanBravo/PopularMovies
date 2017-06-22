package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.Data.*;
import com.example.android.popularmovies.Utilities.JsonUtilities;
import com.example.android.popularmovies.Utilities.NetworkUtilities;

import java.net.URL;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity
{
    private static final String MOVIES_POPULAR = "popular";
    private static final String MOVIES_TOP_RATED = "top_rated";
    private static final String NO_MOVIE_ID = "";

    private MovieDataAdapter movieDataAdapter;
    public static MovieData[] moviesData;
    public static GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoadMoviesData(MOVIES_POPULAR);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] selectedMovieData = getMovieData(moviesData[position]);
                Intent intentToStartDetailActivity = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intentToStartDetailActivity.putExtra("Movie", selectedMovieData);
                startActivity(intentToStartDetailActivity);
            }
        });
    }

    private String[] getMovieData(MovieData movieData)
    {
        String[] data = new String[6];
        data[0] = movieData.getOriginalTitle();
        data[1] = movieData.getPosterID();
        data[2] = movieData.getPlotSynopsis();
        data[3] = movieData.getUserRating();
        data[4] = movieData.getReleaseDate();
        data[5] = movieData.getMovieID();
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.sort_by_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_sortBy_popularity)
        {
            LoadMoviesData(MOVIES_POPULAR);
        }
        else
        {
            LoadMoviesData(MOVIES_TOP_RATED);
        }

        return super.onOptionsItemSelected(item);
    }

    private void LoadMoviesData(String sort)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (NetworkUtilities.isOnline(connectivityManager))
        {
            new FetchMoviesTask().execute(sort);
        }
        else
        {
            Toast.makeText(getApplicationContext() ,"No network connection, please try again.", Toast.LENGTH_LONG).show();
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]>
    {
        @Override
        protected MovieData[] doInBackground(String... params)
        {
            URL moviesRequestUrl;
            if (params[0].equals(MOVIES_POPULAR))
            {
                moviesRequestUrl  = NetworkUtilities.buildMoviesURL(MOVIES_POPULAR, NO_MOVIE_ID);
            }
            else
            {
                moviesRequestUrl  = NetworkUtilities.buildMoviesURL(MOVIES_TOP_RATED, NO_MOVIE_ID);
            }
            try
            {
                String response = NetworkUtilities.getResponseFromHttpUrl(moviesRequestUrl);
                MovieData[] arrangedMovieData = JsonUtilities.getArrangedMovieData(response);
                return arrangedMovieData;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieData[] moviesData)
        {
            MainActivity.moviesData = moviesData;
            movieDataAdapter = new MovieDataAdapter(getApplicationContext(), Arrays.asList(moviesData));
            MainActivity.gridView.setAdapter(movieDataAdapter);
        }
    }

}