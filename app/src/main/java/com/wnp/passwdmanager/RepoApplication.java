package com.wnp.passwdmanager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.wnp.passwdmanager.AuthPart.AuthRepo;
import com.wnp.passwdmanager.Network.ApiRepo;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class RepoApplication extends Application {
    public static final String TOKEN = "token";
    public static final String PIN = "pin";
    private ApiRepo mApiRepo;
    private AuthRepo mAuthRepo;
    private static final String SETTINGS = "settings";
    private static SharedPreferences applicationSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        mApiRepo = new ApiRepo();
        mAuthRepo = new AuthRepo(mApiRepo);
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            applicationSettings = EncryptedSharedPreferences.create(SETTINGS, masterKeyAlias, this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public AuthRepo getmAuthRepo() {
        return mAuthRepo;
    }

    public ApiRepo getmApi() {
        return mApiRepo;
    }

    public static String getToken() {
        return applicationSettings
                .getString(TOKEN, "");
    }

    public static void setToken(String token) {
        applicationSettings.edit().putString(TOKEN, token).apply();
    }

    public static void setDefaultPin(String pin) {
        applicationSettings.edit().putString(PIN, pin).apply();
    }

    public static String getPin() {
        return applicationSettings.getString(PIN, "");
    }

    public static RepoApplication from(Context context) {
        return (RepoApplication) context.getApplicationContext();
    }
}
