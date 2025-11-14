package com.example.campusexpensemanagerforstudent.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "CampusExpenseSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_FAILED_ATTEMPTS = "failed_attempts";
    private static final String KEY_LOCK_TIME = "lock_time";
    private static final String KEY_DARK_MODE = "dark_mode";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(int userId, String email, String name, boolean rememberMe) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_NAME, name);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe);
        editor.putInt(KEY_FAILED_ATTEMPTS, 0);
        editor.putLong(KEY_LOCK_TIME, 0);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public boolean isRememberMe() {
        return prefs.getBoolean(KEY_REMEMBER_ME, false);
    }

    // Failed login attempts tracking
    public int getFailedAttempts() {
        return prefs.getInt(KEY_FAILED_ATTEMPTS, 0);
    }

    public void incrementFailedAttempts() {
        int attempts = getFailedAttempts() + 1;
        editor.putInt(KEY_FAILED_ATTEMPTS, attempts);
        if (attempts >= 3) {
            editor.putLong(KEY_LOCK_TIME, System.currentTimeMillis());
        }
        editor.apply();
    }

    public void resetFailedAttempts() {
        editor.putInt(KEY_FAILED_ATTEMPTS, 0);
        editor.putLong(KEY_LOCK_TIME, 0);
        editor.apply();
    }

    public boolean isAccountLocked() {
        long lockTime = prefs.getLong(KEY_LOCK_TIME, 0);
        if (lockTime == 0) return false;

        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lockTime;
        long lockDuration = 5 * 60 * 1000; // 5 minutes

        if (timePassed >= lockDuration) {
            resetFailedAttempts();
            return false;
        }
        return true;
    }

    public long getRemainingLockTime() {
        long lockTime = prefs.getLong(KEY_LOCK_TIME, 0);
        if (lockTime == 0) return 0;

        long currentTime = System.currentTimeMillis();
        long lockDuration = 5 * 60 * 1000; // 5 minutes
        long timePassed = currentTime - lockTime;

        return Math.max(0, lockDuration - timePassed);
    }

    // Dark mode preference
    public boolean isDarkModeEnabled() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean enabled) {
        editor.putBoolean(KEY_DARK_MODE, enabled);
        editor.apply();
    }

    public void updateUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }
}
