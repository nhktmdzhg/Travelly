package com.nhk.travelly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashScreen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        boolean isLogged = sharedPreferences.getBoolean("isLogged", false);
        Intent intent;
        ProgressBar progressBar = findViewById(R.id.progressBar);
        if (isFirstRun) {
            intent = new Intent(SplashScreenActivity.this, OnboardActivity.class);
            TimerTask task = new TimerTask() {
                int progress = 0;

                @Override
                public void run() {
                    progressBar.setProgress(progress);
                    ++progress;
                    if (progress == 100)
                        startApp(intent);
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 0, 5);
        } else {
            if (isLogged) {
                intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                TimerTask task = new TimerTask() {
                    int progress = 0;

                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                        ++progress;
                        if (progress == 100)
                            startApp(intent);
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 0, 10);
                int userId = sharedPreferences.getInt("userId", 0);
                DataHolder.getInstance().currentUser = UserInfo.getUserInfo(userId, SplashScreenActivity.this);
            } else {
                intent = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                TimerTask task = new TimerTask() {
                    int progress = 0;

                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                        ++progress;
                        if (progress == 100)
                            startApp(intent);
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 0, 5);
            }
        }
    }

    private void startApp(Intent intent) {
        startActivity(intent);
        finish();
    }
}