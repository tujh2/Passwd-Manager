package com.wnp.passwdmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PasswordsRepository {
    private static List<Item> mData;
    private String dbName;
    private Context mContext;
    private static final String TABLE_NAME = "PasswordList";

    private SQLiteDatabase passDB;

    PasswordsRepository(String name, Context context) {
        mContext = context;
        passDB = new RepositoryDBHelper(name).getWritableDatabase();
    }

    public List<Item> getData() {
        mData = new ArrayList<>();
        Cursor cursor = passDB
                .query(TABLE_NAME, new String[] {"domain_name", "URL", "username", "password"},
                        null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.getPosition() < cursor.getCount()) {
            Item itemOfList = new Item();
            itemOfList.domain = cursor.getString(cursor.getColumnIndex("domain_name"));
            itemOfList.url = cursor.getString(cursor.getColumnIndex("URL"));
            itemOfList.username = cursor.getString(cursor.getColumnIndex("username"));
            itemOfList.password = cursor.getString(cursor.getColumnIndex("password"));
            mData.add(itemOfList);
            cursor.moveToNext();
        }
        cursor.close();
        return mData;
    }

    class Item {
        String domain, url, username, password;
    }

    class RepositoryDBHelper extends SQLiteOpenHelper {

        RepositoryDBHelper(String name) {
            super(mContext, name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {}

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }
}
