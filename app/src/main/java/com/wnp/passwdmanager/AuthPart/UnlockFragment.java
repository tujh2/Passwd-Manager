package com.wnp.passwdmanager.AuthPart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wnp.passwdmanager.R;

public class UnlockFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_unlcok_fragment, container, false);
        EditText pass = view.findViewById(R.id.unlock_pass);
        view.findViewById(R.id.unlock_button).setOnClickListener(v -> {
            SharedPreferences sharedPreferences = AuthActivity.getInstance().getPreferences(Context.MODE_PRIVATE);
            if(sharedPreferences.getString(getResources().getString(R.string.status), null) != null) {
                if(sharedPreferences.getString(getResources().getString(R.string.pin), "").equals(pass.getText().toString()) ) {
                    AuthActivity.getInstance().switchActivity();
                }
            }
        });
        return view;
    }
}
