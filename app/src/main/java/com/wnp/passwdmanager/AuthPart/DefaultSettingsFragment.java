package com.wnp.passwdmanager.AuthPart;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wnp.passwdmanager.R;
import com.wnp.passwdmanager.RepoApplication;

public class DefaultSettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.default_settings_fragment, container, false);
        EditText pinText = view.findViewById(R.id.pin);
        view.findViewById(R.id.finish_but).setOnClickListener(v -> {
            AuthActivity.getInstance().getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                    .putString(getResources().getString(R.string.pin), pinText.getText().toString())
                    .putString(getResources().getString(R.string.status), "logged")
                    .putString("token", RepoApplication.getToken())
                    .apply();
            AuthActivity.getInstance().switchActivity();
        });
        return view;
    }
}
