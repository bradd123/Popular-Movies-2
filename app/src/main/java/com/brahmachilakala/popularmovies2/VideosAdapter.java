package com.brahmachilakala.popularmovies2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brahma on 21/08/17.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {
    private List<Video> mVideos;

    public VideosAdapter(ArrayList<Video> videos) {
        this.mVideos = videos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvVideoName.setText(mVideos.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton ibVideo;
        TextView tvVideoName;

        public ViewHolder(View itemView) {
            super(itemView);

            ibVideo = (ImageButton) itemView.findViewById(R.id.ib_video);
            tvVideoName = (TextView) itemView.findViewById(R.id.tv_video_name);
        }
    }
}
