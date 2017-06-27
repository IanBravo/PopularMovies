package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Data.MovieTrailerData;
import com.example.android.popularmovies.Utilities.JsonUtilities;
import com.example.android.popularmovies.Utilities.NetworkUtilities;

import java.net.URL;

public class ReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]>
{
    private static final String MOVIE_REVIEWS = "reviews";
    private static final String REVIEW_QUERY = "query";
    private static final int REVIEW_LOADER = 10;

    private ReviewsAdapter mReviewsAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        mReviewsAdapter = new ReviewsAdapter();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mReviewsAdapter);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null)
        {
            if (intentThatStartedThisActivity.hasExtra("MovieID"))
            {
                loadReviews(intentThatStartedThisActivity.getStringExtra("MovieID"));
            }
        }

    }

    public boolean checkForEmptyReview(String[] reviews)
    {
        if (reviews.length > 0)
            return false;
        Toast.makeText(this ,"There are no reviews, check back later.", Toast.LENGTH_LONG).show();
        return true;
    }

    public void loadReviews(String movieID)
    {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(REVIEW_QUERY, movieID);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<MovieTrailerData[]> moviesSearchLoader = loaderManager.getLoader(REVIEW_LOADER);
        if (moviesSearchLoader == null)
            loaderManager.initLoader(REVIEW_LOADER, queryBundle, this);
        else
            loaderManager.restartLoader(REVIEW_LOADER, queryBundle, this);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args)
    {
        return new AsyncTaskLoader<String[]>(this)
        {
            @Override
            protected void onStartLoading()
            {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public String[] loadInBackground()
            {
                URL reviewsRequestUrl = NetworkUtilities.buildMoviesURL(MOVIE_REVIEWS, args.getString(REVIEW_QUERY));
                try
                {
                    String rawReviewsJson = NetworkUtilities.getResponseFromHttpUrl(reviewsRequestUrl);
                    String[] reviewsData = JsonUtilities.getReviewsData(rawReviewsJson);
                    return reviewsData;
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
    public void onLoadFinished(Loader<String[]> loader, String[] reviewsData)
    {
        if (checkForEmptyReview(reviewsData))
            finish();
        mReviewsAdapter.setReviews(reviewsData);
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) { }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
