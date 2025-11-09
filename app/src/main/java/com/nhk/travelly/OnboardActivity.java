package com.nhk.travelly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OnboardActivity extends AppCompatActivity {
    int layoutID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.onboarding_1);
        layoutID = R.layout.onboarding_1;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboarding_1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void onNext(View view) {
        if (layoutID == R.layout.onboarding_1) {
            setContentView(R.layout.onboarding_2);
            layoutID = R.layout.onboarding_2;
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboarding_2), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            findViewById(R.id.onboarding_2).requestApplyInsets();
        } else if (layoutID == R.layout.onboarding_2) {
            setContentView(R.layout.onboarding_3);
            layoutID = R.layout.onboarding_3;
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboarding_3), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            findViewById(R.id.onboarding_3).requestApplyInsets();
        } else if (layoutID == R.layout.onboarding_3) {
            Intent intent = new Intent(OnboardActivity.this, WelcomeActivity.class);
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstRun", false);
            editor.apply();
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutID == R.layout.onboarding_3) {
            setContentView(R.layout.onboarding_2);
            layoutID = R.layout.onboarding_2;
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboarding_2), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            findViewById(R.id.onboarding_2).requestApplyInsets();
        } else if (layoutID == R.layout.onboarding_2) {
            setContentView(R.layout.onboarding_1);
            layoutID = R.layout.onboarding_1;
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.onboarding_1), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            findViewById(R.id.onboarding_1).requestApplyInsets();
        } else
            super.onBackPressed();
    }
}