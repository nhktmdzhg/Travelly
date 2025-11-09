package com.nhk.travelly;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UserInfo {
    public int id;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String phone;
    public String avatarUri;

    public UserInfo(int id, String firstName, String lastName, String email, String password, String phone, String avatarUri) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.avatarUri = avatarUri;
    }

    public void saveNewUserInfo(@NonNull Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int numUsers = sharedPreferences.getInt("numUsers", 0);
        ++numUsers;
        id = numUsers;
        editor.putInt("numUsers", numUsers);
        editor.putString(numUsers + "_firstName", firstName);
        editor.putString(numUsers + "_lastName", lastName);
        editor.putString(numUsers + "_email", email);
        editor.putString(numUsers + "_password", password);
        editor.putString(numUsers + "_phone", phone);
        editor.putString(numUsers + "_avatarUri", avatarUri);

        editor.apply();
    }

    @Nullable
    public static UserInfo getUserInfo(String email, @NonNull Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        int numUsers = sharedPreferences.getInt("numUsers", 0);
        for (int i = 1; i <= numUsers; i++) {
            String userEmail = sharedPreferences.getString(i + "_email", "");
            if (userEmail.equals(email))
                return new UserInfo(i, sharedPreferences.getString(i + "_firstName", ""),
                        sharedPreferences.getString(i + "_lastName", ""),
                        sharedPreferences.getString(i + "_email", ""),
                        sharedPreferences.getString(i + "_password", ""),
                        sharedPreferences.getString(i + "_phone", ""),
                        sharedPreferences.getString(i + "_avatarUri", ""));
        }
        return null;
    }

    @Nullable
    public static UserInfo getUserInfo(int id, @NonNull Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        int numUsers = sharedPreferences.getInt("numUsers", 0);
        if (id > numUsers)
            return null;

        return new UserInfo(id, sharedPreferences.getString(id + "_firstName", ""),
                sharedPreferences.getString(id + "_lastName", ""),
                sharedPreferences.getString(id + "_email", ""),
                sharedPreferences.getString(id + "_password", ""),
                sharedPreferences.getString(id + "_phone", ""),
                sharedPreferences.getString(id + "_avatarUri", ""));
    }

    public void editAndSave(@NonNull Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(id + "_firstName", firstName);
        editor.putString(id + "_lastName", lastName);
        editor.putString(id + "_email", email);
        editor.putString(id + "_password", password);
        editor.putString(id + "_phone", phone);
        saveImageToInternalStorage(mContext);

        editor.apply();
    }

    private void saveImageToInternalStorage(Context mContext) {
        try {
            if (avatarUri.isEmpty())
                return;

            InputStream inputStream = mContext.getContentResolver().openInputStream(Uri.parse(avatarUri));
            if (inputStream == null) {
                Log.e("UserInfo", "saveImageToInternalStorage: inputStream is null");
                return;
            }
            File imageFile = new File(mContext.getFilesDir(), id + "_avatar.jpg");
            OutputStream outputStream = mContext.openFileOutput(imageFile.getName(), Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);

            inputStream.close();
            outputStream.close();
            avatarUri = imageFile.getAbsolutePath();
            mContext.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit().putString(id + "_avatarUri", avatarUri).apply();
        } catch (IOException e) {
            Log.e("UserInfo", "saveImageToInternalStorage: " + e.getMessage());
        }
    }
}
