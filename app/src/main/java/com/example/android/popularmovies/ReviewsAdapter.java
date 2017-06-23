package com.example.android.popularmovies;

/**
 * Created by Ian Bravo on 22-Jun-17.
 */

import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder>
{
    private String[] reviews;

    public ReviewsAdapter(){}

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView mReviewTextView;

        public ReviewsAdapterViewHolder(View view)
        {
            super(view);
            mReviewTextView = (TextView) view.findViewById(R.id.tv_review);
        }
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position)
    {
        String review = reviews[position];
        holder.mReviewTextView.setText(review);
    }

    @Override
    public int getItemCount()
    {
        if (reviews == null)
            return 0;
        return reviews.length;
    }

    public void setReviews(String[] reviewsData)
    {
        reviews = reviewsData;
        notifyDataSetChanged();
    }
}
