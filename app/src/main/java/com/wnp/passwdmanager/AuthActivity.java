package com.wnp.passwdmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import com.wnp.passwdmanager.AuthFragments.LoginFragment;
import com.wnp.passwdmanager.AuthFragments.UnlockFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {
    private static Navigator navigator;
    private static AuthActivity INSTANCE;
    public final NetworkManager.OnRequestCompleteListener listener = body -> runOnUiThread(() -> {
        if( body.has(getResources().getString(R.string.status)) ) {
            try {
                if( body.getString(getResources().getString(R.string.status)).equals("logged") ){
                    AuthActivity.getInstance().getPreferences(Context.MODE_PRIVATE).edit().putString("status", "logged")
                            .putString(getResources().getString(R.string.pin), "admin")
                            .apply();
                    switchActivity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                Toast.makeText(getApplicationContext(),body.getString("code"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        INSTANCE = this;
        navigator = new Navigator();

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        if( sharedPreferences.getString(getResources().getString(R.string.status), "unauth").equals("logged")) {
            navigator.navigateToFragment(new UnlockFragment(), false);
        } else if(savedInstanceState == null)
            navigator.navigateToFragment(new LoginFragment(), false);
    }

    public void switchActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    public static Navigator getNavigator() {
        return navigator;
    }
    public static AuthActivity getInstance() {
        return INSTANCE;
    }
    @Override
    protected void onDestroy() {
        NetworkManager.getInstance().clear();
        super.onDestroy();
    }

    private class Navigator implements FragmentNavigator{
        private final FragmentManager fragmentManager = getSupportFragmentManager();

        @Override
        public void navigateToFragment(Fragment frag, boolean backStack) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.auth_fragment, frag);
            if(backStack)
                fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
