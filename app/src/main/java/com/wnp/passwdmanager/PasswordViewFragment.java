package com.wnp.passwdmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wnp.passwdmanager.Database.DatabaseManager;

public class PasswordViewFragment extends Fragment {
    private DatabaseManager.Item password;

    static PasswordViewFragment newInstance(DatabaseManager.Item passwordItem) {
        PasswordViewFragment fragment = new PasswordViewFragment();
        fragment.setPassword(passwordItem);
        return fragment;
    }
    void setPassword(DatabaseManager.Item pass) {
        password = pass;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tmp = view.findViewById(R.id.username_view);
        tmp.setText(password.username);
        tmp = view.findViewById(R.id.url_view);
        tmp.setText(password.url);
        tmp = view.findViewById(R.id.password_view);
        tmp.setText(password.password);
    }
}
