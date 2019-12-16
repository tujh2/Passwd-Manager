package com.wnp.passwdmanager.Account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.wnp.passwdmanager.AuthPart.AuthActivity;
import com.wnp.passwdmanager.Network.ApiRepo;
import com.wnp.passwdmanager.Network.LoginApi;
import com.wnp.passwdmanager.RepoApplication;

import java.io.IOException;

import retrofit2.Call;

public class Authenticator extends AbstractAccountAuthenticator {
    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, AuthActivity.class);

        final Bundle bundle = new Bundle();
        if(options != null) {
            bundle.putAll(options);
        }
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        final Bundle result = new Bundle();
        final AccountManager am = AccountManager.get(mContext.getApplicationContext());
        ApiRepo api = RepoApplication.from(mContext).getmApi();
        String authToken = am.peekAuthToken(account, authTokenType);
        if(TextUtils.isEmpty(authToken)) {
            final String password = am.getPassword(account);
            if(!TextUtils.isEmpty(password)) {
                try {
                    LoginApi.Response call = api.getmAuthApi().Auth(new LoginApi.UserPlain(account.name, password)).execute().body();
                    authToken = call.token;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(!TextUtils.isEmpty(authToken)) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        }
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
