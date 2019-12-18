package com.wnp.passwdmanager;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.wnp.passwdmanager.Database.PasswordEntity;
import com.wnp.passwdmanager.Database.PasswordsRepository;

import java.util.List;

public class PasswordsViewModel extends AndroidViewModel {
    private PasswordsRepository passwordsRepository;
    private LiveData<List<PasswordEntity>> allPasswords;

    public PasswordsViewModel(@NonNull Application application) {
        super(application);
        passwordsRepository = new PasswordsRepository(application);
        allPasswords = passwordsRepository.readAll();
    }

    LiveData<List<PasswordEntity>> getAllPasswords() {
        return allPasswords;
    }

    void insert(PasswordEntity passwordEntity) {
        passwordsRepository.insert(passwordEntity);
    }
}
