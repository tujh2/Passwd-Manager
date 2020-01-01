package com.wnp.passwdmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;

import com.wnp.passwdmanager.AuthPart.AuthActivity;
import com.wnp.passwdmanager.AuthPart.UnlockFragment;
import com.wnp.passwdmanager.Database.EncryptionWorker;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements FragmentNavigator {

    private UUID decryptRequestID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //navigateToFragment(new MainFragment(), false);
        if(savedInstanceState == null) {
            navigateToFragment(new UnlockFragment(), false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public UUID getDecryptRequestID() {
        return decryptRequestID;
    }

    public void setDecryptRequestID(UUID id) {
       decryptRequestID =  id;
    }

    @Override
    protected void onStop() {
        navigateToFragment(new UnlockFragment(), false);
        Data data = new Data.Builder()
                .putString(EncryptionWorker.TYPE, EncryptionWorker.ENCRYPT).build();
        OneTimeWorkRequest encryptRequest = new OneTimeWorkRequest.
                Builder(EncryptionWorker.class)
                .setInputData(data).build();
        WorkManager.getInstance().enqueue(encryptRequest);
        super.onStop();
    }

    public void navigateToFragment(Fragment frag, boolean backStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().replace(R.id.mainFragment, frag);
        if (backStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
