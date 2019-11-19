package com.wnp.passwdmanager;

import android.app.Application;
import android.content.Context;

import com.wnp.passwdmanager.AuthPart.AuthRepo;
import com.wnp.passwdmanager.Network.ApiRepo;

public class RepoApplication extends Application {
    private ApiRepo mApiRepo;
    private AuthRepo mAuthRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        mApiRepo = new ApiRepo();
        mAuthRepo = new AuthRepo(mApiRepo);
    }

    public AuthRepo getmAuthRepo() { return mAuthRepo; }
    public ApiRepo getmApi() { return mApiRepo; }

    public static RepoApplication from(Context context) {
        return (RepoApplication) context.getApplicationContext();
    }
}
