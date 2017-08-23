package com.brahmachilakala.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.brahmachilakala.popularmovies2.data.MovieContract;
import com.brahmachilakala.popularmovies2.data.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    ArrayList<Movie> movies;
    RecyclerView rvMovies;
    MoviesAdapter rvAdapter;
    GestureDetector mGestureDetector;

    String sortOrder = "popular";

    private SQLiteDatabase mDb;
    private int favoriteMoviesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSharedPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        sortOrder = sharedPreferences.getString("sort_order", "popular");

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        loadMovies();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("sort_order")) {
            sortOrder = sharedPreferences.getString("sort_order", "popular");
        }

        loadMovies();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void loadMovies() {

        if (isNetworkAvailable()) {

            if (sortOrder.equals("favorite")) {
                Toast.makeText(this, "favorite clicked", Toast.LENGTH_SHORT).show();
            } else {
                new GetMoviesTask().execute("https://api.themoviedb.org/3/movie/" + sortOrder + "?api_key=3afb8ecfbf45f15fa5dc9463f48976ed");
            }
        }
    }

    private class GetMoviesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream in = conn.getInputStream();

                StringBuilder stringBuilder = new StringBuilder();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return stringBuilder.toString();


            } catch (Exception e) {
                Log.i("MainActivity", "error in parsing the url" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            try {

                JSONObject response = new JSONObject(s);

                JSONArray moviesArray = response.getJSONArray("results");

                movies = Movie.fromJson(moviesArray);

                runRecyclerView();


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void runRecyclerView() {
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);

        rvAdapter = new MoviesAdapter(this, movies);

        rvMovies.setAdapter(rvAdapter);

        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
//        rvMovies.setNestedScrollingEnabled(false);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        rvMovies.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());

                if (childView != null && mGestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(childView);

                    Movie movie = movies.get(position);

                    Intent detailActivity = new Intent(MainActivity.this, DetailActivity.class);
                    detailActivity.putExtra("id", movie.getId());
                    detailActivity.putExtra("original_title", movie.getOriginalTitle());
                    detailActivity.putExtra("image_url", movie.getImageUrl());
                    detailActivity.putExtra("overview", movie.getOverview());
                    detailActivity.putExtra("user_rating", movie.getUserRating());
                    detailActivity.putExtra("release_date", movie.getReleaseDate());
                    detailActivity.putExtra("is_favorite", movie.isFavorite());
                    startActivity(detailActivity);

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private void getFavoriteMovies() {
        MovieDbHelper dbHelper = new MovieDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
        Cursor cursor = getDbCursor();
        favoriteMoviesCount = cursor.getCount();
        ArrayList<Integer> movieIds = new ArrayList<>();

        if (favoriteMoviesCount == 0) {
            Toast.makeText(this, "No favorite movies", Toast.LENGTH_SHORT).show();
        } else {
            for (int i=0; i<cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                movieIds.add(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            }
        }

        for(int j=0; j<movieIds.size(); j++) {
            int movieId = movieIds.get(j);
            new MovieTask().execute("https://api.themoviedb.org/3/movie/" + movieId + "?api_key=3afb8ecfbf45f15fa5dc9463f48976ed");
        }

    }

    private Cursor getDbCursor() {
        return mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID
        );
    }

    private class MovieTask extends AsyncTask<String, Void, String> {

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
                Log.i("MainActivity", "Error in parsing MoviesTask URL " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);

                Movie movie = Movie.fromJson(jsonObject);
                movie.setAsFavorite(true);
                movies.add(movie);

                if (movies.size() == favoriteMoviesCount) {
                    runRecyclerView();
                }
            } catch (Exception e) {
                Log.i("MainActivity", "Error in parsing MoviesTask JSON " + e.getMessage());
            }
        }
    }
}
