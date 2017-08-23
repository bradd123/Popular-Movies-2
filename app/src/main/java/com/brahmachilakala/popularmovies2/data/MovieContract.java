package com.brahmachilakala.popularmovies2.data;

import android.provider.BaseColumns;

/**
 * Created by brahma on 23/08/17.
 */

public class MovieContract {


    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movieId";

        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
    }
}
