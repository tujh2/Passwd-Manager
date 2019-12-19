package com.wnp.passwdmanager.AuthPart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.wnp.passwdmanager.FragmentNavigator;
import com.wnp.passwdmanager.MainActivity;
import com.wnp.passwdmanager.R;
import com.wnp.passwdmanager.RepoApplication;

public class AuthActivity extends AppCompatActivity implements FragmentNavigator{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        if(!RepoApplication.getToken().equals("") && !RepoApplication.getPin().equals(""))
            navigateToFragment(new UnlockFragment(), false);
        else if(savedInstanceState == null)
            navigateToFragment(new LoginFragment(), false);
    }

    public void switchActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void navigateToFragment(Fragment frag, boolean backStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction().replace(R.id.auth_fragment, frag);
        if(backStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
