package com.wnp.passwdmanager;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.wnp.passwdmanager.AuthPart.AuthRepo;
import com.wnp.passwdmanager.Database.PasswordsRepository;
import com.wnp.passwdmanager.Network.ApiRepo;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class RepoApplication extends Application {
    public static final String TOKEN = "token";
    public static final String PIN = "pin";
    public static final String SYNCNUM = "syncNumber";
    private ApiRepo mApiRepo;
    private AuthRepo mAuthRepo;
    private PasswordsRepository passwordsRepository;
    private static final String SETTINGS = "settings";
    private static SharedPreferences applicationSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        mApiRepo = new ApiRepo();
        mAuthRepo = new AuthRepo(mApiRepo);
        passwordsRepository = new PasswordsRepository(this);
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            applicationSettings = EncryptedSharedPreferences.create(SETTINGS, masterKeyAlias, this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public PasswordsRepository getPasswordsRepository() {
        return passwordsRepository;
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

    public static int getCurrentSyncNumber() {
        return applicationSettings.getInt(SYNCNUM, 0);
    }

    public static void setCurrentSyncNumber(int num) {
        applicationSettings.edit().putInt(SYNCNUM, num).apply();
    }

    public static RepoApplication from(Context context) {
        return (RepoApplication) context.getApplicationContext();
    }
}
