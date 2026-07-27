package com.example.portalnexus.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(String token, String name, String email, boolean keepLoggedIn) {
        editor.putString(Constants.KEY_TOKEN, token);
        editor.putString(Constants.KEY_USER_NAME, name);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.putBoolean(Constants.KEY_KEEP_LOGGED_IN, keepLoggedIn);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(Constants.KEY_TOKEN, null);
    }

    public String getUserName() {
        return sharedPreferences.getString(Constants.KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(Constants.KEY_USER_EMAIL, null);
    }

    public boolean isKeepLoggedInEnabled() {
        return sharedPreferences.getBoolean(Constants.KEY_KEEP_LOGGED_IN, false);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
