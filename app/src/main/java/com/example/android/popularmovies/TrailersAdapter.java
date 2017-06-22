package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.Data.MovieTrailerData;

/**
 * Created by Ian Bravo on 21-Jun-17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder>
{
    private MovieTrailerData[] mTrailerData;
    private final TrailerAdapterOnClickHandler mClickHandler;

    public interface TrailerAdapterOnClickHandler
    {
        void onClick(MovieTrailerData trailerData);
    }

    public TrailersAdapter(TrailerAdapterOnClickHandler clickHandler)
    {
        mClickHandler = clickHandler;
    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder holder, int position)
    {
        MovieTrailerData trailerData = mTrailerData[position];
        holder.mTrailerTextView.setText(trailerData.getName());
    }

    @Override
    public int getItemCount()
    {
        if (mTrailerData == null)
            return 0;
        return mTrailerData.length;
    }

    public void setTrailerData(MovieTrailerData[] trailerData)
    {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener
    {
        public final TextView mTrailerTextView;

        public TrailersAdapterViewHolder(View view)
        {
            super(view);
            mTrailerTextView = (TextView) view.findViewById(R.id.tv_trailer_info);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int position = getAdapterPosition();
            MovieTrailerData trailerInfo = mTrailerData[position];
            mClickHandler.onClick(trailerInfo);

        }
    }
}