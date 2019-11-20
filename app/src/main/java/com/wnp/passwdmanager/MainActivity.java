package com.wnp.passwdmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements FragmentNavigator {
    private static MainActivity INSTANCE;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        INSTANCE = this;
        navigateToFragment(new MainFragment(), false);
    }

    static MainActivity getInstance() {
        return INSTANCE;
    }

    @Override
    public void navigateToFragment(Fragment frag, boolean backStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().replace(R.id.mainFragment, frag);
        if(backStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


}
