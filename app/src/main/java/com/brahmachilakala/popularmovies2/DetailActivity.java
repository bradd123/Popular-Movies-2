package com.brahmachilakala.popularmovies2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView tvMovieTitle;
    private ImageView ivMovieThumbnail;
    private TextView tvMovieYear;
    private TextView tvMovieDuration;
    private TextView tvMovieRating;
    private TextView btFavorite;
    private TextView tvMovieOverview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        ivMovieThumbnail = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        tvMovieYear = (TextView) findViewById(R.id.tv_movie_year);
        tvMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
        tvMovieOverview = (TextView) findViewById(R.id.tv_movie_overview);

        tvMovieTitle.setText(getIntent().getStringExtra("original_title"));
        Picasso.with(this).load(getIntent().getStringExtra("image_url")).into(ivMovieThumbnail);
        tvMovieOverview.setText(getIntent().getStringExtra("overview"));
        tvMovieYear.setText(getIntent().getStringExtra("release_date"));
        tvMovieRating.setText(getIntent().getStringExtra("user_rating"));

    }
}
