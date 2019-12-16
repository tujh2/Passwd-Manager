package com.wnp.passwdmanager.Account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AuthenticatorService extends Service {
    private Authenticator mAuthenticator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new Authenticator(getApplicationContext());
    }
}
