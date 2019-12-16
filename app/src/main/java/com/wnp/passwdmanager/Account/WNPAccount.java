package com.wnp.passwdmanager.Account;

import android.accounts.Account;
import android.os.Parcel;

public class WNPAccount extends Account {

    public static final String TYPE = "com.wnp.passwd.wnpAccount";
    public static final String TOKEN_FULL_ACCESS = "com.github.elegion.TOKEN_FULL_ACCESS";
    public static final String KEY_PASSWORD = "com.github.elegion.KEY_PASSWORD";

    public WNPAccount(Parcel in) {
        super(in);
    }
    public WNPAccount(String name) {
        super(name, TYPE);
    }
}
