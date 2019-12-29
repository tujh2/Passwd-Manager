package com.wnp.passwdmanager;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.wnp.passwdmanager.Database.PasswordEntity;
import com.wnp.passwdmanager.Database.PasswordsRepository;

import java.util.List;

public class PasswordsViewModel extends AndroidViewModel {
    private final PasswordsRepository passwordsRepository;

    public PasswordsViewModel(@NonNull Application application) {
        super(application);
        passwordsRepository =
                RepoApplication.from(application.getApplicationContext())
                        .getPasswordsRepository();
    }

    LiveData<List<PasswordEntity>> getAllPasswords() {
        return passwordsRepository.readAll();
    }

    void insert(PasswordEntity passwordEntity) {
        RepoApplication.setCurrentSyncNumber(RepoApplication.getCurrentSyncNumber() + 1);
        Log.d("viewModel", "" + RepoApplication.getCurrentSyncNumber());
        passwordsRepository.insert(passwordEntity);
    }

    void delete(PasswordEntity passwordEntity) {
        RepoApplication.setCurrentSyncNumber(RepoApplication.getCurrentSyncNumber() + 1);
        passwordsRepository.delete(passwordEntity);
    }

    void update(PasswordEntity passwordEntity) {
        RepoApplication.setCurrentSyncNumber(RepoApplication.getCurrentSyncNumber() + 1);
        passwordsRepository.update(passwordEntity);
    }
}
