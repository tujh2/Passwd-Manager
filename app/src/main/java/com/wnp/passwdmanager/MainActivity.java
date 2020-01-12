package com.wnp.passwdmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;

import com.wnp.passwdmanager.AuthPart.DefaultSettingsFragment;
import com.wnp.passwdmanager.AuthPart.UnlockFragment;
import com.wnp.passwdmanager.Database.EncryptionWorker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final String ENCRYPT_WORK_TAG = "ENCRYPT_TAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {
            if (RepoApplication.getPin().equals("")) {
                navigateToFragment(new DefaultSettingsFragment(), null);
            } else {
                navigateToFragment(new UnlockFragment(), null);
            }
        }
    }

    @Override
    protected void onResume() {
        if (RepoApplication.from(getApplicationContext()).isLocked) {
            navigateToFragment(new UnlockFragment(), null);
        } else WorkManager.getInstance().cancelAllWorkByTag(ENCRYPT_WORK_TAG);
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing())
            RepoApplication.from(getApplicationContext()).getPasswordsRepository().close();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Data data = new Data.Builder()
                .putString(EncryptionWorker.TYPE, EncryptionWorker.ENCRYPT).build();
        OneTimeWorkRequest encryptRequest = new OneTimeWorkRequest.
                Builder(EncryptionWorker.class)
                .addTag(ENCRYPT_WORK_TAG)
                .setInitialDelay(5, TimeUnit.SECONDS)
                .setInputData(data).build();
        WorkManager.getInstance().enqueue(encryptRequest);
        super.onStop();
    }

    public void navigateToFragment(Fragment frag, String backStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().replace(R.id.mainFragment, frag);
        if (backStack != null)
            fragmentTransaction.addToBackStack(backStack);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackStackChanged() {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(canGoBack);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }
}
