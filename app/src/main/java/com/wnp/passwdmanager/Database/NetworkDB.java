package com.wnp.passwdmanager.Database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

@Database(entities = {PasswordEntity.class}, version = 1, exportSchema = false)
abstract class NetworkDB extends RoomDatabase {

    abstract PasswordDao getPasswordDao();
    private static NetworkDB instance;

    static synchronized NetworkDB getInstance(Context context) {
        if(instance == null)
            instance = create(context);
        return instance;
    }

    private static NetworkDB create(Context context) {
        return Room.databaseBuilder(context,
                NetworkDB.class,
                "networkDB.db").build();
    }


}
