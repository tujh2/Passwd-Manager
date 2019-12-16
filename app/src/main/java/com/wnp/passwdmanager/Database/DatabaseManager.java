package com.wnp.passwdmanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wnp.passwdmanager.Network.ApiRepo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatabaseManager {
    private static int dbVersion = 1;
    private Context context;
    private static final String dbName = "userPasswords.db";
    private SQLiteDatabase database;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static DatabaseManager INSTANCE = new DatabaseManager();

    public static DatabaseManager getInstance(Context context) {
        INSTANCE.context = context.getApplicationContext();
        return INSTANCE;
    }

    public void insert(Item item) {
        executor.execute(() -> {
            insertRoom(item);
        });
    }

    private void insertRoom(Item item) {
        PasswordEntity passwordEntity = new PasswordEntity();
        passwordEntity.domain_name = item.domain;
        passwordEntity.URL = item.url;
        passwordEntity.username = item.username;
        passwordEntity.password = item.password;
        AppDatabase.getInstance(context).getPasswordDao().insertAll(passwordEntity);
    }

    public void readAll(final ReadListener<Item> listener) {
        executor.execute(() -> {
            readAllRoom(listener);
        });
    }

    private void readAllRoom(ReadListener<Item> listener) {
        List<PasswordEntity> list = AppDatabase.getInstance(context).getPasswordDao().getAllEntities();
        ArrayList<Item> items = new ArrayList<>();
        for(PasswordEntity entity : list) {
            Item tmp = new Item();
            tmp.domain = entity.domain_name;
            tmp.url = entity.URL; tmp.username = entity.username;
            tmp.password = entity.password;
            items.add(tmp);
        }
        listener.onRead(items);
    }

    public static class Item {
        public String domain, url, username, password;
    }

    public interface ReadListener<T> {
        void onRead(final List<T> items);
    }
}
