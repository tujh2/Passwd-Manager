package com.wnp.passwdmanager;

import android.app.Application;
import android.content.Context;

import com.wnp.passwdmanager.AuthPart.AuthRepo;
import com.wnp.passwdmanager.Network.ApiRepo;
import com.wnp.passwdmanager.Network.DatabaseApi;

public class RepoApplication extends Application {
    private ApiRepo mApiRepo;
    private AuthRepo mAuthRepo;
    private PasswordsRepository passwordsRepository;
    private DatabaseRepo mDatabaseRepo;
    private static String token = "";

    @Override
    public void onCreate() {
        super.onCreate();
        mApiRepo = new ApiRepo();
        mAuthRepo = new AuthRepo(mApiRepo);
        mDatabaseRepo = new DatabaseRepo(mApiRepo);

        token = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getString("token", "");
    }

    public AuthRepo getmAuthRepo() { return mAuthRepo; }
    public ApiRepo getmApi() { return mApiRepo; }
    public DatabaseRepo getDatabaseRepo() { return mDatabaseRepo; }

    public static String getToken() {
        return token;
    }

    public static void setToken(String t) {
        token = t;
    }

    public static RepoApplication from(Context context) {
        return (RepoApplication) context.getApplicationContext();
    }
}
