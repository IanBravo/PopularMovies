package com.example.android.popularmovies.Data;

/**
 * Created by Ian Bravo on 21-Jun-17.
 */

public class MovieTrailerData
{
    String id;
    String name;

    public MovieTrailerData(String id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }
}
