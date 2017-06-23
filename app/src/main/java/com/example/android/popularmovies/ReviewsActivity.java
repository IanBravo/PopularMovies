package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Utilities.JsonUtilities;
import com.example.android.popularmovies.Utilities.NetworkUtilities;

import java.net.URL;

public class ReviewsActivity extends AppCompatActivity
{
    private static final String MOVIE_REVIEWS = "reviews";

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
        {
            return false;
        }
        Toast.makeText(this ,"There are no reviews, check back later.", Toast.LENGTH_LONG).show();
        return true;
    }

    public void loadReviews(String movieID)
    {
        new FetchReviewsTask().execute(movieID);
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, String[]>
    {
        @Override
        protected String[] doInBackground(String... params)
        {
            URL reviewsRequestUrl = NetworkUtilities.buildMoviesURL(MOVIE_REVIEWS, params[0]);
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

        @Override
        protected void onPostExecute(String[] reviewsData)
        {
            if (checkForEmptyReview(reviewsData))
                finish();
            mReviewsAdapter.setReviews(reviewsData);
        }
    }
}
