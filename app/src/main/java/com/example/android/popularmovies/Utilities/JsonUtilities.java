package com.example.android.popularmovies.Utilities;

import com.example.android.popularmovies.Data.MovieData;
import com.example.android.popularmovies.Data.MovieTrailerData;

import org.json.*;

/**
 * Created by Ian Bravo on 24-May-17.
 */

public final class JsonUtilities
{
    public static MovieData[] getArrangedMovieData(String movieJsonString) throws JSONException
    {
        MovieData[] moviesData= null;
        JSONObject moviesJson = new JSONObject(movieJsonString);
        JSONArray moviesArray = moviesJson.getJSONArray("results");
        moviesData = new MovieData[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++)
        {
            String originalTitle = moviesArray.getJSONObject(i).getString("original_title");
            String posterID = moviesArray.getJSONObject(i).getString("poster_path");
            String plotSynopsis = moviesArray.getJSONObject(i).getString("overview");
            String userRating = moviesArray.getJSONObject(i).getString("vote_average");
            String releaseDate = moviesArray.getJSONObject(i).getString("release_date");
            String id = moviesArray.getJSONObject(i).getString("id");

            moviesData[i] = new MovieData(originalTitle, posterID, plotSynopsis, userRating, releaseDate, id);
        }
        return moviesData;
    }

    public static MovieTrailerData[] getTrailersData(String trailersJson) throws JSONException
    {
        JSONObject moviesJson = new JSONObject(trailersJson);
        JSONArray moviesArray = moviesJson.getJSONArray("results");
        MovieTrailerData[] trailerData = new MovieTrailerData[moviesArray.length()];

        for (int i = 0; i < moviesArray.length(); i++)
        {
            String id = moviesArray.getJSONObject(i).getString("key");
            String name = moviesArray.getJSONObject(i).getString("name");

            trailerData[i] = new MovieTrailerData(id, name);
        }
        return trailerData;
    }
}
