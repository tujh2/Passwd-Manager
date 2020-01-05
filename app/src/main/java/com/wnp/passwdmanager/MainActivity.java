package com.wnp.passwdmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.wnp.passwdmanager.AuthPart.UnlockFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FragmentNavigator, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //navigateToFragment(new MainFragment(), false);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if(savedInstanceState == null) {
            navigateToFragment(new UnlockFragment(), null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if(isFinishing())
            Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if(isFinishing())
            Log.d(TAG, "onStop");
        super.onStop();
    }

    public void navigateToFragment(Fragment frag, String backStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().replace(R.id.mainFragment, frag);
        if(backStack != null)
            fragmentTransaction.addToBackStack(backStack);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackStackChanged() {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>0;
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(canGoBack);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }
}
