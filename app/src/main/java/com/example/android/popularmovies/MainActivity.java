package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MovieData[]>
{
    private static final String MOVIES_POPULAR = "popular";
    private static final String MOVIES_TOP_RATED = "top_rated";
    private static final String MOVIES_FAVORITE = "favorites";
    private static final String MOVIES_QUERY = "query";
    private static final String NO_MOVIE_ID = "";
    private static final int MOVIES_LOADER = 12;

    private static String movieListToLoad = "popular";

    private MovieDataAdapter movieDataAdapter;

    public static MovieData[] moviesData;
    public static GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMovieDetailActivity(getMovieData(moviesData[position]));
            }
        });

        if (savedInstanceState != null)
        {
            movieListToLoad = savedInstanceState.getString(MOVIES_QUERY);
            if (movieListToLoad.equals(MOVIES_POPULAR))
                LoadMoviesData(MOVIES_POPULAR);
            else if (movieListToLoad.equals(MOVIES_TOP_RATED))
                LoadMoviesData(MOVIES_TOP_RATED);
            else
                loadFavorites();
        }
        else
        {
            if (movieListToLoad.equals(MOVIES_FAVORITE))
                loadFavorites();
            else
                LoadMoviesData(movieListToLoad);
        }
    }

    private void startMovieDetailActivity(String[] selectedMovieData)
    {
        Intent intentToStartDetailActivity = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intentToStartDetailActivity.putExtra("Movie", selectedMovieData);
        startActivity(intentToStartDetailActivity);
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
            LoadMoviesData(MOVIES_POPULAR);
        else if (id == R.id.action_sortBy_rated)
            LoadMoviesData(MOVIES_TOP_RATED);
        else
            loadFavorites();

        return super.onOptionsItemSelected(item);
    }

    private void loadFavorites()
    {
        Cursor favoritesQuery = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (favoritesQuery.getCount() == 0)
        {
            Toast.makeText(getApplicationContext(), "You have no favorite movies.", Toast.LENGTH_LONG).show();
            return;
        }
        movieListToLoad = MOVIES_FAVORITE;
        displayFavoriteMovies(favoritesQuery);
    }

    private void displayFavoriteMovies(Cursor favoritesQuery)
    {
        MovieData[] data = new MovieData[favoritesQuery.getCount()];
        int i = 0;
        while (favoritesQuery.moveToNext())
        {
            String originalTitle = favoritesQuery.getString(favoritesQuery.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
            String posterID = favoritesQuery.getString(favoritesQuery.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_ID));
            String plotSynopsis = favoritesQuery.getString(favoritesQuery.getColumnIndex(MoviesContract.MovieEntry.COLUMN_PLOT));
            String userRating = favoritesQuery.getString(favoritesQuery.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RATING));
            String releaseDate = favoritesQuery.getString(favoritesQuery.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE));
            String id = favoritesQuery.getString(favoritesQuery.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID));

            data[i] = new MovieData(originalTitle, posterID, plotSynopsis, userRating, releaseDate, id);
            i++;
        }
        attachAdapterToGrid(data);
    }

    public void attachAdapterToGrid(MovieData[] data)
    {
        moviesData = data;
        movieDataAdapter = new MovieDataAdapter(getApplicationContext(), Arrays.asList(moviesData));
        MainActivity.gridView.setAdapter(movieDataAdapter);
    }

    private void LoadMoviesData(String sort)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (NetworkUtilities.isOnline(connectivityManager))
            executeLoader(sort);
        else
            Toast.makeText(getApplicationContext() ,"No network connection, please try again.", Toast.LENGTH_LONG).show();
    }

    private void executeLoader(String sort)
    {
        Bundle queryBundle = new Bundle();
        movieListToLoad = sort;
        queryBundle.putString(MOVIES_QUERY, sort);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<MovieData[]> moviesSearchLoader = loaderManager.getLoader(MOVIES_LOADER);
        if (moviesSearchLoader == null)
            loaderManager.initLoader(MOVIES_LOADER, queryBundle, this);
        else
            loaderManager.restartLoader(MOVIES_LOADER, queryBundle, this);
    }


    @Override
    public Loader<MovieData[]> onCreateLoader(int id, final Bundle args)
    {
        return new AsyncTaskLoader<MovieData[]>(this)
        {
            @Override
            protected void onStartLoading()
            {
                super.onStartLoading();
                if (args == null)
                    return;
                forceLoad();
            }

            @Override
            public MovieData[] loadInBackground()
            {
                URL moviesRequestUrl = (args.getString(MOVIES_QUERY).equals(MOVIES_POPULAR)) ?
                        NetworkUtilities.buildMoviesURL(MOVIES_POPULAR, NO_MOVIE_ID) :
                        NetworkUtilities.buildMoviesURL(MOVIES_TOP_RATED, NO_MOVIE_ID);
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
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieData[]> loader, MovieData[] data)
    {
        attachAdapterToGrid(data);
    }

    @Override
    public void onLoaderReset(Loader<MovieData[]> loader) {}

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIES_QUERY, movieListToLoad);
    }
}