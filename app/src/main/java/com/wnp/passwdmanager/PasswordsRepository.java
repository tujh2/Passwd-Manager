package com.wnp.passwdmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wnp.passwdmanager.Database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class PasswordsRepository {
    private static List<DatabaseManager.Item> mData;
    private DatabaseManager manager;

    PasswordsRepository(Context context) {
        manager = DatabaseManager.getInstance(context);
        mData = new ArrayList<>();
    }

    List<DatabaseManager.Item> getData() {
        return mData;
    }
}
