package com.example.android.popularmovies.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Ian Bravo on 24-May-17.
 */

public class NetworkUtilities
{
    //TODO(1): Insert your TheMovieDB API Key
    private static final String API_KEY = "?api_key=";
    private static final String MOVIES_QUERY_URL = "http://api.themoviedb.org/3/movie/";
    private static final String MOVIE_POPULAR = "popular";
    private static final String MOVIE_TOP_RATED = "top_rated";
    private static final String MOVIE_TRAILERS = "videos";
    private static final String MOVIE_TRAILER_LANGUAGE = "&language=en-US";
    private static final String MOVIE_REVIEW = "reviews";
    private static final String MOVIE_REVIEW_LANGUAGE = "&language=en-US&page=1";

    public static URL buildMoviesURL(String choice, String movieID)
    {
        URL url = null;
        Uri uri = null;
        switch (choice)
        {
            case MOVIE_POPULAR:
                uri = Uri.parse(MOVIES_QUERY_URL + MOVIE_POPULAR + API_KEY);
                break;

            case MOVIE_TOP_RATED:
                uri = Uri.parse(MOVIES_QUERY_URL + MOVIE_TOP_RATED + API_KEY);
                break;

            case MOVIE_TRAILERS:
                uri  = Uri.parse(MOVIES_QUERY_URL + movieID + "/" + MOVIE_TRAILERS + API_KEY + MOVIE_TRAILER_LANGUAGE);
                break;

            case MOVIE_REVIEW:
                uri = Uri.parse(MOVIES_QUERY_URL + movieID + "/" + MOVIE_REVIEW + API_KEY + MOVIE_REVIEW_LANGUAGE);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri");
        }

        try
        {
            url = new URL(uri.toString());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        return url;
    }

    public static boolean isOnline(ConnectivityManager connectivityManager)
    {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException
    {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try
        {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput)
            {
                return scanner.next();
            }
            else
            {
                return null;
            }
        }
        finally
        {
            urlConnection.disconnect();
        }
    }
}
