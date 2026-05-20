package com.example.rentalapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {

    private static final String PREFS_FILE  = "rental_secure_prefs";
    private static final String KEY_TOKEN   = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE    = "user_role";
    private static final String KEY_NAME    = "user_name";

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private TokenManager(Context context) {
        SharedPreferences sp;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            sp = EncryptedSharedPreferences.create(
                    context,
                    PREFS_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback do zwykłych prefs (nie powinno się zdarzyć na SDK 24+)
            sp = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        }
        this.prefs = sp;
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }

    // --- Token ---

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean hasToken() {
        return getToken() != null;
    }

    // --- Dane użytkownika ---

    public void saveUserInfo(int userId, String role, String name) {
        prefs.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_ROLE, role)
                .putString(KEY_NAME, name)
                .apply();
    }

    public int getUserId()   { return prefs.getInt(KEY_USER_ID, -1); }
    public String getUserRole() { return prefs.getString(KEY_ROLE, "user"); }
    public String getUserName() { return prefs.getString(KEY_NAME, ""); }

    public boolean isAdmin() { return "admin".equals(getUserRole()); }

    // --- Wylogowanie ---

    public void clear() {
        prefs.edit().clear().apply();
    }
}
