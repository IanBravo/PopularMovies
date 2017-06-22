package com.example.android.popularmovies.Data;

/**
 * Created by Ian Bravo on 24-May-17.
 */

public class MovieData
{
    String originalTitle;
    String posterID;
    String plotSynopsis;
    String userRating;
    String releaseDate;
    String id;

    public MovieData(String originalTitle, String posterID, String plotSynopsis, String userRating, String releaseDate, String id)
    {
        this.originalTitle = originalTitle;
        this.posterID = posterID;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    public String getOriginalTitle()
    {
        return originalTitle;
    }

    public String getPosterID()
    {
        return posterID;
    }

    public String getPlotSynopsis()
    {
        return plotSynopsis;
    }

    public String getUserRating()
    {
        return userRating;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }

    public String getMovieID() { return id; }
}
