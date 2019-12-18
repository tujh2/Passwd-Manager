package com.wnp.passwdmanager.Database;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PasswordsRepository {
    private PasswordDao passwordDao;
    private LiveData<List<PasswordEntity>> allPasswords;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public PasswordsRepository(@NonNull Application application) {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        passwordDao = database.getPasswordDao();
        allPasswords = passwordDao.getAllEntities();
    }

    public void insert(PasswordEntity passwordEntity) {
        executor.execute(() -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            passwordDao.insert(passwordEntity);
        });
    }

    public LiveData<List<PasswordEntity>> readAll() {
            return allPasswords;
    }
}
