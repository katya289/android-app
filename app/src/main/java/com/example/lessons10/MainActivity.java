package com.example.lessons10;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;




public class MainActivity extends AppCompatActivity {
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        authService = new AuthService();
    }

    public void signUp(View view) {
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();
        authService.signUp(this, email, password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authService.handleActivityResult(requestCode, resultCode, data, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(String accessToken) {
                Toast.makeText(MainActivity.this, "Token received: " + accessToken, Toast.LENGTH_SHORT).show();
                authService.startMainPageActivity(MainActivity.this, accessToken); // Передача accessToken
            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, "Authorization error", Toast.LENGTH_SHORT).show();
            }
        }, this);
    }


}