package com.wnp.passwdmanager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.wnp.passwdmanager.AuthPart.AuthRepo;
import com.wnp.passwdmanager.Network.ApiRepo;
import com.wnp.passwdmanager.Network.DatabaseApi;

public class RepoApplication extends Application {
    private ApiRepo mApiRepo;
    private AuthRepo mAuthRepo;
    private PasswordsRepository passwordsRepository;
    private DatabaseRepo mDatabaseRepo;
    private static RepoApplication INSTANCE;
    private static SharedPreferences applicationSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        mApiRepo = new ApiRepo();
        mAuthRepo = new AuthRepo(mApiRepo);
        mDatabaseRepo = new DatabaseRepo(mApiRepo);
        INSTANCE = this;
        applicationSettings = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public AuthRepo getmAuthRepo() { return mAuthRepo; }
    public ApiRepo getmApi() { return mApiRepo; }
    public DatabaseRepo getDatabaseRepo() { return mDatabaseRepo; }

    public static String getToken() {
        return applicationSettings
                .getString("token", "");
    }

    public static void setToken(String token) {
        applicationSettings.edit().putString("token", token).apply();
    }

    public static void setDefaultPin(String pin) {
        applicationSettings.edit().putString("pin", pin).apply();
    }

    public static String getPin() {
        return applicationSettings.getString("pin", "");
    }

    public static RepoApplication from(Context context) {
        return (RepoApplication) context.getApplicationContext();
    }
}
