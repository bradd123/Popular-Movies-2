package com.brahmachilakala.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brahmachilakala.popularmovies2.data.MovieContract;
import com.brahmachilakala.popularmovies2.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private TextView tvMovieTitle;
    private ImageView ivMovieThumbnail;
    private TextView tvMovieYear;
    private TextView tvMovieDuration;
    private TextView tvMovieRating;
    private Button btFavorite;
    private TextView tvMovieOverview;

    int id;
    ArrayList<Video> mVideos;
    RecyclerView rvVideos;
    VideosAdapter mVideosAdapter;
    GestureDetector mGestureDetector;
    RecyclerView rvReviews;
    ReviewsAdapter mReviewsAdapter;
    ArrayList<Review> mReviews;
    boolean isFavorite = false;

    String baseVideoUrl = "https://www.youtube.com/watch?v=";

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MovieDbHelper dbHelper = new MovieDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        tvMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        ivMovieThumbnail = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        tvMovieYear = (TextView) findViewById(R.id.tv_movie_year);
        tvMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
        tvMovieOverview = (TextView) findViewById(R.id.tv_movie_overview);
        btFavorite = (Button) findViewById(R.id.bt_favorite);

        btFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFavorite) {
                    isFavorite = false;
                    btFavorite.setText("Mark As Favorite");
                    Toast.makeText(DetailActivity.this, "marked it as unfavorite", Toast.LENGTH_SHORT).show();
                    removeMovieFromFavorites(id);

                } else {
                    isFavorite = true;
                    btFavorite.setText("Favorited");
                    Toast.makeText(DetailActivity.this, "marked it as favorite", Toast.LENGTH_SHORT).show();
                    insertNewFavoriteMovie(id, tvMovieTitle.getText().toString());
                }
            }
        });

        id = getIntent().getIntExtra("id", 100);
        tvMovieTitle.setText(getIntent().getStringExtra("original_title"));
        Picasso.with(this).load(getIntent().getStringExtra("image_url")).into(ivMovieThumbnail);
        tvMovieOverview.setText(getIntent().getStringExtra("overview"));
        tvMovieYear.setText(getIntent().getStringExtra("release_date"));
        tvMovieRating.setText(getIntent().getStringExtra("user_rating"));
        isFavorite = getIntent().getBooleanExtra("is_favorite", false);

        if (isFavorite) {
            btFavorite.setText("Favorited");
        }

        getVideos();
        getReviews();

    }

    private boolean removeMovieFromFavorites(int id) {
        return mDb.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + id, null) > 0;
    }

    private long insertNewFavoriteMovie(int id, String title) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
        return mDb.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
    }

    private void getVideos() {
        new VideosTask().execute("https://api.themoviedb.org/3/movie/" + id + "/videos?api_key=3afb8ecfbf45f15fa5dc9463f48976ed");
    }

    private void getReviews() {
        new ReviewsTask().execute("https://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=3afb8ecfbf45f15fa5dc9463f48976ed");
    }

    private void showVideosInRecyclerView() {
        rvVideos = (RecyclerView) findViewById(R.id.rvVideos);

        mVideosAdapter = new VideosAdapter(mVideos);
        rvVideos.setAdapter(mVideosAdapter);

        rvVideos.setLayoutManager(new LinearLayoutManager(this));

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        rvVideos.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View subView = rv.findChildViewUnder(e.getX(), e.getY());

                if (subView != null && mGestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(subView);

                    String videoUrl = baseVideoUrl + mVideos.get(position).getKey();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void showReviews() {
        rvReviews = (RecyclerView) findViewById(R.id.rvReviews);

        mReviewsAdapter = new ReviewsAdapter(mReviews);
        rvReviews.setAdapter(mReviewsAdapter);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
    }

    private class VideosTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder stringBuilder = new StringBuilder();

                String line = "";

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return stringBuilder.toString();


            } catch (Exception e) {
                Log.i("DetailActivity", "Error in getting the video url" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                mVideos = Video.fromJson(jsonArray);

                showVideosInRecyclerView();

            } catch (Exception e) {
                Log.i("DetailActivity", "Error in parsing the JSON" + e.getMessage());
            }
        }
    }

    private class ReviewsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream in = conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                StringBuilder stringBuilder = new StringBuilder();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return stringBuilder.toString();


            } catch (Exception e) {
                Log.i("DetailActivity", "Error in parsing reviews URL " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                mReviews = Review.fromJson(jsonObject.getJSONArray("results"));

                showReviews();
            } catch (Exception e) {
                Log.i("DetailActivity", "Error in parsing JSON " + e.getMessage());
            }
        }
    }
}
