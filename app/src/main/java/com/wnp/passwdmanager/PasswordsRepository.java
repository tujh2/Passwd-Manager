package com.wnp.passwdmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class PasswordsRepository {
    private static final String DOMAIN_NAME = "domain_name";
    private static final String URL = "URL";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static List<Item> mData;
    private Context mContext;
    private static final String TABLE_NAME = "PasswordList";
    private SQLiteDatabase passDB;

    PasswordsRepository(Context context) {
        mContext = context;
        //RepoApplication.from(mContext).getDatabaseRepo().getDatabase();
    }

    public List<Item> getData() {
        passDB = new RepositoryDBHelper().getWritableDatabase();
        mData = new ArrayList<>();
        Cursor cursor = passDB
                .query(TABLE_NAME, new String[] {DOMAIN_NAME, URL, USERNAME, PASSWORD},
                        null, null, null, null, null);
        cursor.moveToFirst();
        while (cursor.getPosition() < cursor.getCount()) {
            Item itemOfList = new Item();
            itemOfList.domain = cursor.getString(cursor.getColumnIndex(DOMAIN_NAME));
            itemOfList.url = cursor.getString(cursor.getColumnIndex(URL));
            itemOfList.username = cursor.getString(cursor.getColumnIndex(USERNAME));
            itemOfList.password = cursor.getString(cursor.getColumnIndex(PASSWORD));
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

        RepositoryDBHelper() {
            super(mContext, mContext.getFilesDir().getAbsolutePath() + "/" + "userPasswords.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {}

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    }
}
