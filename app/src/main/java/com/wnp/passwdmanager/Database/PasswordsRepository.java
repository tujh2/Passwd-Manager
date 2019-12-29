package com.wnp.passwdmanager.Database;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.wnp.passwdmanager.RepoApplication;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PasswordsRepository {
    private PasswordDao passwordDao;
    private LiveData<List<PasswordEntity>> allPasswords;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static Application app;
    AppDatabase database;

    public PasswordsRepository(@NonNull Application application) {
        app = application;
        database = AppDatabase.getInstance(app.getApplicationContext());
        passwordDao = database.getPasswordDao();
        allPasswords = passwordDao.getAllEntities();
    }

    public void insert(PasswordEntity passwordEntity) {
        if(passwordDao == null || allPasswords == null)
            reopenDatabase();
        executor.execute(() -> {
            RepoApplication.setCurrentSyncNumber(RepoApplication.getCurrentSyncNumber() + 1);
            passwordDao.insert(passwordEntity);
        });
    }

    public void update(PasswordEntity passwordEntity) {
        if(passwordDao == null || allPasswords == null)
            reopenDatabase();
        executor.execute(() -> {
            RepoApplication.setCurrentSyncNumber(RepoApplication.getCurrentSyncNumber() + 1);
            passwordDao.update(passwordEntity);
        });
    }

    public void delete(PasswordEntity passwordEntity) {
        if(passwordDao == null || allPasswords == null)
            reopenDatabase();
        executor.execute(() -> {
            RepoApplication.setCurrentSyncNumber(RepoApplication.getCurrentSyncNumber() + 1);
            passwordDao.delete(passwordEntity);
        });
    }

    public LiveData<List<PasswordEntity>> readAll() {
        if(passwordDao == null || allPasswords == null)
            reopenDatabase();
        return allPasswords;
    }

    public void close() {
        if (database.isOpen()) {
            database.close();
            passwordDao = null;
            allPasswords = null;
        }
    }

    private void reopenDatabase() {
        database = AppDatabase.getInstance(app.getApplicationContext());
        passwordDao = database.getPasswordDao();
        allPasswords = passwordDao.getAllEntities();
    }
}
