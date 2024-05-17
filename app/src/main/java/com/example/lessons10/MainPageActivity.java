package com.example.lessons10;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class MainPageActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SpotifyService spotifyService;
    private TracksPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setClipToPadding(false);
        viewPager.setPadding(40, 0, 40, 0);
        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                page.setTranslationX(-40 * position);
            }
        });

        spotifyService = new SpotifyService();
        String accessToken = getIntent().getStringExtra("ACCESS_TOKEN");

        if (accessToken != null) {
            spotifyService.getTracks(accessToken, new SpotifyService.TracksCallback() {
                @Override
                public void onSuccess(List<Track> tracks) {
                    runOnUiThread(() -> {
                        adapter = new TracksPagerAdapter(MainPageActivity.this, tracks);
                        viewPager.setAdapter(adapter);
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
    // Добавление иконки в ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }

    // Обработка нажатия на иконку в ActionBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_user_profile) {
            // Обработка нажатия на иконку пользователя
            // Здесь вы можете открыть активность с информацией о пользователе или выполнить другое действие
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
