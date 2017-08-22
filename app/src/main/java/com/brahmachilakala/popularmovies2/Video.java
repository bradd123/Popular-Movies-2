package com.brahmachilakala.popularmovies2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by brahma on 21/08/17.
 */

public class Video {
    private String name;
    private String key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static Video fromJson(JSONObject jsonObject) {
         Video video = new Video();

        try {
            video.key = jsonObject.getString("key");
            video.name = jsonObject.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return video;
    }

    public static ArrayList<Video> fromJson(JSONArray jsonArray) {
        ArrayList<Video> videos = new ArrayList<>();

        for (int i=0; i < jsonArray.length(); i++) {
            try {
                videos.add(fromJson(jsonArray.getJSONObject(i)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return videos;
    }
}
