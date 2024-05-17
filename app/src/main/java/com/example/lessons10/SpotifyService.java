package com.example.lessons10;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyService {
    private static final String TAG = "SpotifyService";
    private static final String BASE_URL = "https://api.spotify.com/v1/";

    private final OkHttpClient client;

    public SpotifyService() {
        client = new OkHttpClient();
    }
    public void getUserInfo(String accessToken, final UserInfoCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "me")
                .header("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch user info", e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to fetch user info: " + response.code());
                    callback.onFailure(new IOException("Failed to fetch user info: " + response.code()));
                    return;
                }

                try {
                    JSONObject json = new JSONObject(response.body().string());
                    String username = json.getString("display_name");
                    String email = json.getString("email");
                    callback.onSuccess(username, email);
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse user info response", e);
                    callback.onFailure(e);
                }
            }
        });
    }

    public void getTracks(String accessToken, final TracksCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "tracks/?ids=7ouMYWpwJ422jRcDASZB7P,4VqPOruhp5EdPBeR92t6lQ,2takcwOaAZWiXQijPHIx7B")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "failed to get tracks", e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to fetch tracks info: " + response.code());
                    callback.onFailure(new IOException("Failed to fetch tracks info: " + response.code()));
                    return;
                }
                try {

                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    JSONArray tracksArray = jsonResponse.getJSONArray("tracks");
                    List<Track> tracks = new ArrayList<>();
                    for (int i = 0; i < tracksArray.length(); i++) {
                        JSONObject trackObject = tracksArray.getJSONObject(i);
                        Track track = parseTrack(trackObject);
                        tracks.add(track);
                    }
                    callback.onSuccess(tracks);
                }
                catch (Exception e) {
                    Log.e(TAG, "Failed to parse tracks info response", e);
                    callback.onFailure(e);
                }
            }
        });
    }
    private Track parseTrack(JSONObject trackObject) throws JSONException {
        Track track = new Track();
        track.setName(trackObject.getString("name"));
        track.setType(trackObject.getString("type"));
        JSONArray imagesArray = trackObject.getJSONObject("album").getJSONArray("images");
        track.setImageUrl(imagesArray.getJSONObject(0).getString("url"));
        track.setPreviewUrl(trackObject.getString("preview_url"));
        JSONArray artistsArray = trackObject.getJSONArray("artists");
        JSONObject artist = artistsArray.getJSONObject(0);
        track.setArtistName(artist.getString("name"));
        return track;
    }


    public interface UserInfoCallback {
        void onSuccess(String username, String email);
        void onFailure(Exception e);
    }

    public interface TracksCallback {
        void onSuccess(List<Track> tracks);
        void onFailure(Exception e);
    }

}