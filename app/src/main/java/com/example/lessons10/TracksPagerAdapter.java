package com.example.lessons10;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

public class TracksPagerAdapter extends RecyclerView.Adapter<TracksPagerAdapter.TrackViewHolder> {
    private Context context;
    private List<Track> tracks;
    private MediaPlayer mediaPlayer;

    public TracksPagerAdapter(Context context, List<Track> tracks) {
        this.context = context;
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.track_page, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = tracks.get(position);

        holder.nameTextView.setText(track.getName());
        holder.typeTextView.setText(track.getType());
        holder.artistNameTextView.setText(track.getArtistName());

        Glide.with(context).load(track.getImageUrl()).into(holder.trackImageView);

        holder.playButton.setOnClickListener(v -> playPreview(track.getPreviewUrl()));
        holder.stopButton.setOnClickListener(v -> stopPreview());
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        ImageView trackImageView;
        TextView nameTextView;
        TextView typeTextView;
        TextView artistNameTextView;
        Button playButton;
        Button stopButton;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            trackImageView = itemView.findViewById(R.id.trackImageView);
            nameTextView = itemView.findViewById(R.id.trackNameTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            artistNameTextView = itemView.findViewById(R.id.artistNameTextView);
            playButton = itemView.findViewById(R.id.playButton);
            stopButton = itemView.findViewById(R.id.stopButton);
        }
    }

    private void playPreview(String previewUrl) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (previewUrl != null && !previewUrl.isEmpty()) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(previewUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to play track", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Preview not available for this track", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPreview() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
