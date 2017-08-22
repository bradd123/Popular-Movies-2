package com.brahmachilakala.popularmovies2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by brahma on 22/08/17.
 */

public class Review {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Review fromJson(JSONObject jsonObject) {
        Review review = new Review();

        try {
            review.content= jsonObject.getString("content");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return review;
    }

    public static ArrayList<Review> fromJson(JSONArray jsonArray) {
        ArrayList<Review> reviews = new ArrayList<>();

        for (int i=0; i<jsonArray.length(); i++) {
            try {
                reviews.add(fromJson(jsonArray.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return reviews;
    }
}
