package com.wnp.passwdmanager.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;



@Database(entities = {PasswordEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PasswordDao getPasswordDao();
    private static AppDatabase instance;

    static synchronized AppDatabase getInstance(Context context) {
        if(instance == null)
            instance = create(context);
        else if(!instance.isOpen())
            instance = create(context);
        return instance;
    }

    @Override
    public void close() {
        super.close();
    }

    private static AppDatabase create(Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class,
                "userPasswords.db").build();
    }
}
