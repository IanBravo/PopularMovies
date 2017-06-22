package com.example.android.popularmovies.Data;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ian Bravo on 24-May-17.
 */

public class MovieDataAdapter extends ArrayAdapter<MovieData>
{
    public MovieDataAdapter(Context context, List<MovieData> moviesData)
    {
        super (context, 0, moviesData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MovieData movieData = getItem(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, parent, false);
        }

        ImageView posterView = (ImageView) convertView.findViewById(R.id.movie_image);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500/" + movieData.posterID).into(posterView);

        return convertView;
    }


}
