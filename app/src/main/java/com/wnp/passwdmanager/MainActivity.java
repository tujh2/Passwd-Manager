package com.wnp.passwdmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;

import com.wnp.passwdmanager.AuthPart.UnlockFragment;
import com.wnp.passwdmanager.Database.EncryptionWorker;
import com.wnp.passwdmanager.Network.SyncWorker;

public class MainActivity extends AppCompatActivity implements FragmentNavigator {

    public static final String SYNC_WORKER = "syncWorker";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //navigateToFragment(new MainFragment(), false);
        if(savedInstanceState == null) {
            navigateToFragment(new UnlockFragment(), false);
        }

    }

    public void navigateToFragment(Fragment frag, boolean backStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().replace(R.id.mainFragment, frag);
        if (backStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
