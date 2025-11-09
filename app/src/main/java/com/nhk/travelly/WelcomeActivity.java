package com.nhk.travelly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeActivity extends AppCompatActivity {
    private int layoutId;
    private boolean showPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.welcome);
        layoutId = R.layout.welcome;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainWelcome), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void buttonSignUp(View view) {
        showPassword = false;
        setContentView(R.layout.sign_up);
        layoutId = R.layout.sign_up;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainSignUpLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.mainSignUpLayout).requestApplyInsets();
    }

    public void buttonSignIn(View view) {
        showPassword = false;
        setContentView(R.layout.sign_in);
        layoutId = R.layout.sign_in;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainSignInLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.mainSignInLayout).requestApplyInsets();
    }

    public void toggleShowHidePassword(View view) {
        showPassword = !showPassword;
        EditText passwordInput;
        if (layoutId == R.layout.sign_in)
            passwordInput = findViewById(R.id.passwordInput);
        else
            passwordInput = findViewById(R.id.passwordInput2);
        if (showPassword)
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        else
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void backToWelcome(View view) {
        setContentView(R.layout.welcome);
        layoutId = R.layout.welcome;
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainWelcome), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.mainWelcome).requestApplyInsets();
    }

    @Override
    public void onBackPressed() {
        if (layoutId == R.layout.sign_in || layoutId == R.layout.sign_up) {
            setContentView(R.layout.welcome);
            layoutId = R.layout.welcome;
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainWelcome), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            findViewById(R.id.mainWelcome).requestApplyInsets();
        } else
            super.onBackPressed();
    }

    public void realSignIn(View view) {
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkInvalidEmail(email)) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }
        UserInfo user = UserInfo.getUserInfo(email, this);
        if (user == null) {
            Toast.makeText(this, "User not found, try again or sign up", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!user.password.equals(password)) {
            Toast.makeText(this, "Incorrect password, try again", Toast.LENGTH_SHORT).show();
            return;
        }
        DataHolder.getInstance().currentUser = user;
        Intent intent = new Intent(this, HomeActivity.class);
        SharedPreferences.Editor editor = getSharedPreferences("MyAppPreferences", MODE_PRIVATE).edit();
        editor.putBoolean("isLogged", true);
        editor.putInt("userId", user.id);
        editor.apply();
        startActivity(intent);
        finish();
    }

    private boolean checkInvalidEmail(String email) {
        return !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void signInWithSocial(@NonNull View view) {
        String link;
        if (view.getId() == R.id.googleSignIn)
            link = "https://accounts.google.com";
        else if (view.getId() == R.id.fbSignIn)
            link = "https://www.facebook.com";
        else
            link = "https://id.apple.com";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        Toast.makeText(this, "This feature currently not available, so I will direct you to the website", Toast.LENGTH_SHORT).show();
        startActivity(browserIntent);
    }

    public void buttonForgotPassword(View view) {
        Toast.makeText(this, "This feature currently not available", Toast.LENGTH_SHORT).show();
    }

    public void realSignUp(View view) {
        EditText emailInput = findViewById(R.id.emailInput2);
        EditText passwordInput = findViewById(R.id.passwordInput2);
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkInvalidEmail(email)) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }
        UserInfo user = UserInfo.getUserInfo(email, this);
        if (user != null) {
            Toast.makeText(this, "User already exists, try again", Toast.LENGTH_SHORT).show();
            return;
        }
        user = new UserInfo(-1, "", "", email, password, "", "");
        user.saveNewUserInfo(this);
        Toast.makeText(this, "Sign up successful, please sign in", Toast.LENGTH_SHORT).show();
    }
}