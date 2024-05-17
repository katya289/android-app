package com.example.lessons10;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    private ListView lvAudiobooks;
    private SpotifyService spotifyService;

    private TextView nameTextView;
    private TextView typeTextView;
    private ImageView trackImageView;
    private Button playButton;
    private MediaPlayer mediaPlayer;

    private TextView artistNameTextView;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        trackImageView = findViewById(R.id.trackImageView);
        nameTextView = findViewById(R.id.trackNameTextView);
        typeTextView = findViewById(R.id.typeTextView);
        artistNameTextView = findViewById(R.id.artistNameTextView);
        stopButton = findViewById(R.id.stopButton);
        playButton = findViewById(R.id.playButton);
        spotifyService = new SpotifyService();
        String accessToken = getIntent().getStringExtra("ACCESS_TOKEN");

        if (accessToken != null) {
            spotifyService.getTracks(accessToken, new SpotifyService.TracksCallback() {
                @Override
                public void onSuccess(String name, String type, String imageUrl, String previewUrl, String artistName) {
                    runOnUiThread(() -> {
                        nameTextView.setText(name);
                        typeTextView.setText(type);
                        artistNameTextView.setText(artistName);

                        Glide.with(MainPageActivity.this).load(imageUrl).into(trackImageView);
                        playButton.setOnClickListener(v -> {
                            if (previewUrl != null && !previewUrl.isEmpty()) {
                                try {
                                    mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setDataSource(previewUrl);
                                    mediaPlayer.prepareAsync();
                                    mediaPlayer.setOnPreparedListener(mp -> {
                                        mediaPlayer.start();
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainPageActivity.this, "Failed to play track", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                // Preview URL is not available
                                Toast.makeText(MainPageActivity.this, "Preview not available for this track", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                    stopButton.setOnClickListener(v -> {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    });

                }

                @Override
                public void onFailure(Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(MainPageActivity.this, "Failed to fetch tracks info", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        } else {

            Toast.makeText(this, "Access token not found", Toast.LENGTH_SHORT).show();
        }
    }

}
