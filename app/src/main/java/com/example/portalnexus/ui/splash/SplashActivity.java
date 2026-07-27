package com.example.portalnexus.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.portalnexus.databinding.ActivitySplashBinding;
import com.example.portalnexus.ui.home.HomeActivity;
import com.example.portalnexus.ui.menu.MenuActivity;
import com.example.portalnexus.utils.SessionManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SessionManager sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn() && sessionManager.isKeepLoggedInEnabled()) {
                intent = new Intent(SplashActivity.this, MenuActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}
