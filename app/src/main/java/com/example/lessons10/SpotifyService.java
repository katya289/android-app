package com.example.lessons10;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyService {
    private static final String TAG = "SpotifyService";
    private static final String BASE_URL = "https://api.spotify.com/v1/";

    private OkHttpClient client;

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

    public interface UserInfoCallback {
        void onSuccess(String username, String email);
        void onFailure(Exception e);
    }
}
