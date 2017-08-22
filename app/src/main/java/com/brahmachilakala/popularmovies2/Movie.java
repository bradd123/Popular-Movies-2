package com.brahmachilakala.popularmovies2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by brahma on 12/07/17.
 */

public class Movie {
    private int id;
    private String originalTitle;
    private String baseUrl = "https://image.tmdb.org/t/p/w185";
    private String imageUrl;
    private String overview;
    private String userRating;
    private String releaseDate;
    private boolean isFavorite = false;

    public int getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOverview() {
        return overview;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setAsFavorite(boolean fav) {
        isFavorite = fav;
    }

    public static Movie fromJson(JSONObject json) {

        Movie movie = new Movie();

        try {
            movie.id = json.getInt("id");
            movie.originalTitle = json.getString("original_title");
            movie.imageUrl = movie.getBaseUrl() + json.getString("poster_path");
            movie.overview = json.getString("overview");
            movie.userRating = String.valueOf(json.getDouble("vote_average"));
            movie.releaseDate = json.getString("release_date");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return movie;
    }

    public static ArrayList<Movie> fromJson(JSONArray jsonArray) {
        ArrayList<Movie> movies = new ArrayList<>();

        for (int i=0; i < jsonArray.length(); i++) {

            try {
                JSONObject json = jsonArray.getJSONObject(i);
                movies.add(fromJson(json));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return movies;
    }
}
