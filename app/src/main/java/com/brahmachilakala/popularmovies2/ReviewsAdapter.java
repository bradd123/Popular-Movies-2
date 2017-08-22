package com.brahmachilakala.popularmovies2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brahma on 22/08/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Review> mReviewList;

    public ReviewsAdapter(ArrayList<Review> reviews) {
        mReviewList = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvMovieReview.setText(mReviewList.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieReview;

        public ViewHolder(View itemView) {
            super(itemView);

            tvMovieReview = (TextView) itemView.findViewById(R.id.tv_movie_review);
        }
    }
}
