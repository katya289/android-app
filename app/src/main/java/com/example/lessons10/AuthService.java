package com.example.lessons10;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class AuthService {
    private static final String CLIENT_ID = "4479fdda449f455281bb1e6aceb7142d";


    private static final String REDIRECT_URI = "com.example.android://oauth2redirect";

    private static final int REQUEST_CODE = 1337;

    private FirebaseAuth mAuth;

    public AuthService() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(Activity activity, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(activity, user.getEmail(), Toast.LENGTH_SHORT).show();
                        startLoginActivity(activity);
                    } else {
                        Toast.makeText(activity, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startLoginActivity(Activity activity) {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-email", "playlist-read-private"});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent intent, AuthCallback callback, Activity activity) {
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthorizationResponse.Type.TOKEN) {
                String accessToken = response.getAccessToken();
                callback.onSuccess(accessToken);
                Toast.makeText(activity, "Token received: " + accessToken, Toast.LENGTH_SHORT).show();
            } else if (response.getType() == AuthorizationResponse.Type.ERROR) {
                callback.onError();
            }
        }
    }

    void startMainPageActivity(Activity activity, String accessToken) {
        Intent intent = new Intent(activity, MainPageActivity.class);
        intent.putExtra("ACCESS_TOKEN", accessToken);
        activity.startActivity(intent);
        activity.finish();
    }

    public interface AuthCallback {
        void onSuccess(String accessToken);
        void onError();
    }
}
