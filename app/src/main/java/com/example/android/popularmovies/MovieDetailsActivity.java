package com.example.android.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.AsyncTask;

import com.example.android.popularmovies.Data.MovieData;
import com.example.android.popularmovies.Data.MovieTrailerData;
import com.example.android.popularmovies.Data.MoviesContract;
import com.example.android.popularmovies.Utilities.JsonUtilities;
import com.example.android.popularmovies.Utilities.NetworkUtilities;
import com.example.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetailsActivity extends AppCompatActivity implements TrailersAdapter.TrailerAdapterOnClickHandler {
    private static final String MOVIE_TRAILERS = "videos";
    private static final String IMAGE_URL = "http://image.tmdb.org/t/p/w780/";
    private static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";

    private TrailersAdapter mTrailerAdapter;
    private String[] movieData = null;

    ActivityMovieDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        mTrailerAdapter = new TrailersAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerviewTrailers.setLayoutManager(layoutManager);
        mBinding.recyclerviewTrailers.setHasFixedSize(true);
        mBinding.recyclerviewTrailers.setAdapter(mTrailerAdapter);

        loadIntentData();
        loadTrailerData();
        mBinding.toggleFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    insertData();
                }
                else
                {
                    delete();
                }
            }
        });

        mBinding.ivReviews.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentToStartDetailActivity = new Intent(MovieDetailsActivity.this, ReviewsActivity.class);
                intentToStartDetailActivity.putExtra("MovieID", movieData[5]);
                startActivity(intentToStartDetailActivity);
            }
        });

        checkIfFavorite();
    }

    @Override
    public void onClick(MovieTrailerData trailerData)
    {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerData.getId()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + trailerData.getId()));

        try
        {
            startActivity(appIntent);
        }
        catch (ActivityNotFoundException ex)
        {
            startActivity(webIntent);
        }
    }

    private void loadTrailerData()
    {
        new FetchTrailersTask().execute();
    }

    private void checkIfFavorite()
    {
        String[] mProjection = {MoviesContract.MovieEntry.COLUMN_MOVIE_ID};
        String mSelectionClause = MoviesContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String [] mSelectionArgs = {movieData[5]};

        Cursor cursor = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                mProjection,
                mSelectionClause,
                mSelectionArgs,
                null);

        if (cursor != null)
        {
            int index = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID);
            String movieId = "";
            while (cursor.moveToNext())
            {
                movieId = cursor.getString(index);
            }
            if (movieId.equals(movieData[5]))
            {
                mBinding.toggleFavorite.setChecked(true);
            }
        }
        else
        {
            mBinding.toggleFavorite.setChecked(false);
        }
    }

    private void loadIntentData()
    {
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null)
        {
            if (intentThatStartedThisActivity.hasExtra("Movie"))
            {
                movieData = intentThatStartedThisActivity.getStringArrayExtra("Movie");
                fillWithMovieData();
            }
        }
    }

    private void fillWithMovieData()
    {
        mBinding.tvMovieTitle.setText(movieData[0]);
        mBinding.tvMoviePlot.setText(movieData[2]);
        mBinding.tvMovieRating.setText(movieData[3] + "/10");
        mBinding.tvMovieReleaseDate.setText(movieData[4]);
        Picasso.with(MovieDetailsActivity.this).load(IMAGE_URL + movieData[1]).into(mBinding.imMoviePoster);
    }

    public void insertData()
    {
        ContentValues movieInformation = prepareForInsert();
        getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movieInformation);
    }

    private ContentValues prepareForInsert()
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, movieData[0]);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_ID, movieData[1]);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_PLOT, movieData[2]);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RATING, movieData[3]);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE, movieData[4]);
        contentValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, movieData[5]);
        return contentValues;
    }

    public void delete()
    {
        getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI, "movieID=?", new String[]{movieData[5]});
    }

    private void showErrorMessage()
    {
        Toast.makeText(getApplicationContext() ,"Error Fetching Trailer Data", Toast.LENGTH_LONG).show();
    }

    public class FetchTrailersTask extends AsyncTask<Void, Void, MovieTrailerData[]>
    {
        @Override
        protected MovieTrailerData[] doInBackground(Void... params)
        {
            URL trailersRequestUrl = NetworkUtilities.buildMoviesURL(MOVIE_TRAILERS, movieData[5]);
            try
            {
                String rawTrailersJson = NetworkUtilities.getResponseFromHttpUrl(trailersRequestUrl);
                MovieTrailerData[] trailersData = JsonUtilities.getTrailersData(rawTrailersJson);
                return trailersData;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieTrailerData[] data)
        {
            if (data != null)
                mTrailerAdapter.setTrailerData(data);
            else
                showErrorMessage();
            super.onPostExecute(data);
        }
    }
}