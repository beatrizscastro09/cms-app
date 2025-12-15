package com.netflix_plus_plus.cms.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "NetflixPlusPlusSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // guardar sessao
    public void createLoginSession(String token, String userId, String username, String email, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.commit();
    }

    // checkar se o user está logado
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // checkar se o user é admin
    public boolean isAdmin() {
        String role = prefs.getString(KEY_ROLE, "");
        return "Admin".equalsIgnoreCase(role);
    }

    // get token
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    // get username
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    // get email
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    // logout
    public void logout() {
        editor.clear();
        editor.commit();
    }
}