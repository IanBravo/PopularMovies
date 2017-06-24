package com.example.android.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by Ian Bravo on 24-May-17.
 */

public class MainActivityFragment extends Fragment
{
    public MainActivityFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        MainActivity.gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        return rootView;
    }
}
