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

    public void update(PasswordEntity passwordEntity) {
        executor.execute(() -> {
            RepoApplication.setCurrentSyncNumber(RepoApplication.getCurrentSyncNumber() + 1);
            passwordDao.update(passwordEntity);
        });
    }

    public void delete(PasswordEntity passwordEntity) {
        executor.execute(() -> {
            RepoApplication.setCurrentSyncNumber(RepoApplication.getCurrentSyncNumber() + 1);
            passwordDao.delete(passwordEntity);
        });
    }

    public LiveData<List<PasswordEntity>> readAll() {
        return allPasswords;
    }

    public void close(Context context) {
        AppDatabase.getInstance(context).close();
    }

    public void reopenDatabase(Context context) {
        passwordDao = AppDatabase.getInstance(context).getPasswordDao();
        allPasswords = passwordDao.getAllEntities();
    }
}
