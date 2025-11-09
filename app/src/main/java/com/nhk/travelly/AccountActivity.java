package com.nhk.travelly;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;

public class AccountActivity extends AppCompatActivity {
    private int layoutId;
    private boolean showPassword = false;
    private boolean canChangePassword = false;
    private static final int GALLERY_REQUEST_CODE = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setLayoutAccount();
    }

    private void setLayoutAccount() {
        setContentView(R.layout.account);
        layoutId = R.layout.account;
        TextView accountName = findViewById(R.id.accountName);
        UserInfo userInfo = DataHolder.getInstance().currentUser;
        if (userInfo.firstName.isEmpty() && userInfo.lastName.isEmpty())
            accountName.setText(R.string.anonymous_user);
        else if (userInfo.firstName.isEmpty())
            accountName.setText(userInfo.lastName);
        else if (userInfo.lastName.isEmpty())
            accountName.setText(userInfo.firstName);
        else
            accountName.setText(String.format("%s %s", userInfo.firstName, userInfo.lastName));

        ShapeableImageView avatarImage = findViewById(R.id.avatar);
        if (userInfo.avatarUri.isEmpty())
            avatarImage.setImageResource(R.drawable.avatar);
        else {
            File imgFile = new File(userInfo.avatarUri);
            if (imgFile.exists())
                avatarImage.setImageURI(Uri.fromFile(imgFile));
            else {
                userInfo.avatarUri = "";
                avatarImage.setImageResource(R.drawable.avatar);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainAccountLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void changeTab(@NonNull View view) {
        int id = view.getId();
        Intent intent;
        if (id == R.id.home)
            intent = new Intent(this, HomeActivity.class);
        else if (id == R.id.booking)
            intent = new Intent(this, BookingActivity.class);
        else {
            Toast.makeText(this, "This feature is not available yet", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void endSession(View view) {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        SharedPreferences.Editor editor = getSharedPreferences("MyAppPreferences", MODE_PRIVATE).edit();
        editor.putBoolean("isLogged", false);
        editor.apply();
        startActivity(intent);
    }

    public void personalInformation(View view) {
        setContentView(R.layout.personal_info);
        EditText firstName = findViewById(R.id.first_name);
        EditText lastName = findViewById(R.id.last_name);
        EditText email = findViewById(R.id.email);
        EditText phone = findViewById(R.id.phone);
        UserInfo userInfo = DataHolder.getInstance().currentUser;
        firstName.setText(userInfo.firstName);
        lastName.setText(userInfo.lastName);
        email.setText(userInfo.email);
        phone.setText(userInfo.phone);
        layoutId = R.layout.personal_info;

        ShapeableImageView avatarImage = findViewById(R.id.avatarImageChange);
        if (userInfo.avatarUri.isEmpty())
            avatarImage.setImageResource(R.drawable.avatar);
        else {
            File imgFile = new File(userInfo.avatarUri);
            if (imgFile.exists())
                avatarImage.setImageURI(Uri.fromFile(imgFile));
            else {
                userInfo.avatarUri = "";
                avatarImage.setImageResource(R.drawable.avatar);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.personal_info), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.personal_info).requestApplyInsets();
    }

    public void notAvailable(View view) {
        Toast.makeText(this, "This feature is not available yet", Toast.LENGTH_SHORT).show();
    }

    public void toggleShowHidePassword(View view) {
        showPassword = !showPassword;
        EditText password = findViewById(R.id.password);
        if (showPassword)
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        else
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public void backToAccount(View view) {
        setLayoutAccount();
        findViewById(R.id.mainAccountLayout).requestApplyInsets();
    }

    @Override
    public void onBackPressed() {
        if (layoutId == R.layout.account)
            super.onBackPressed();
        else {
            setLayoutAccount();
            findViewById(R.id.mainAccountLayout).requestApplyInsets();
        }
    }

    public void changeInfo(View view) {
        EditText firstName = findViewById(R.id.first_name);
        EditText lastName = findViewById(R.id.last_name);
        EditText email = findViewById(R.id.email);
        EditText phone = findViewById(R.id.phone);
        EditText password = findViewById(R.id.password);
        UserInfo userInfo = DataHolder.getInstance().currentUser;
        String pass = password.getText().toString();
        if (!canChangePassword) {
            if (pass.equals(userInfo.password)) {
                Toast.makeText(this, "Can change password, type new password and click again", Toast.LENGTH_SHORT).show();
                canChangePassword = true;
                return;
            } else if (!pass.isEmpty()) {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (pass.isEmpty()) {
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            } else if (pass.equals(userInfo.password)) {
                Toast.makeText(this, "New password cannot be the same as the old one", Toast.LENGTH_SHORT).show();
                return;
            } else {
                userInfo.password = pass;
                canChangePassword = false;
                password.setText("");
            }
        }
        userInfo.firstName = firstName.getText().toString();
        userInfo.lastName = lastName.getText().toString();
        userInfo.email = email.getText().toString();
        userInfo.phone = phone.getText().toString();
        userInfo.editAndSave(AccountActivity.this);
        Toast.makeText(this, "Information changed", Toast.LENGTH_SHORT).show();
    }

    public void changeAvatar(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (GALLERY_REQUEST_CODE == requestCode && resultCode == RESULT_OK) {
            Uri uri = null;
            if (data != null)
                uri = data.getData();
            if (uri != null) {
                ShapeableImageView avatarImage = findViewById(R.id.avatarImageChange);
                avatarImage.setImageURI(uri);
                DataHolder.getInstance().currentUser.avatarUri = uri.toString();
            }
        }
    }
}