package com.wnp.passwdmanager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.wnp.passwdmanager.AuthPart.AuthRepo;
import com.wnp.passwdmanager.Database.EncryptionWorker;
import com.wnp.passwdmanager.Database.PasswordsRepository;
import com.wnp.passwdmanager.Network.ApiRepo;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class RepoApplication extends Application {
    private static final String TOKEN = "token";
    private static final String PIN = "pin";
    private static final String SYNCNUM = "syncNumber";
    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "PASSWORD";
    public static final String ENCRYPTKEY = "ENCRYPTKEY";
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

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("APP", "onTerminate");
        //passwordsRepository.close(this);
    }

    public static String getEncryptionKey() {
        return applicationSettings.getString(ENCRYPTKEY, "B85E079D8FE739C8779125D62B4AFD68");
    }

    public static void setEncryptionKey(String key) {
        applicationSettings.edit().putString(ENCRYPTKEY, key).apply();
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

    public static void setUsername(String user) {
        applicationSettings.edit().putString(LOGIN, user).apply();
    }

    public static String getUsername() {
        return applicationSettings.getString(LOGIN, "");
    }

    public static void setPassword(String pass) {
        applicationSettings.edit().putString(PASSWORD, pass).apply();
    }

    public static String getPassword() {
        return applicationSettings.getString(PASSWORD, "");
    }

    public static RepoApplication from(Context context) {
        return (RepoApplication) context.getApplicationContext();
    }
}
