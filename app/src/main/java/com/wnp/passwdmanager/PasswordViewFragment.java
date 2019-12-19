package com.wnp.passwdmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wnp.passwdmanager.Database.PasswordEntity;

public class PasswordViewFragment extends Fragment {
    private PasswordEntity password;

    static PasswordViewFragment newInstance(PasswordEntity passwordItem) {
        PasswordViewFragment fragment = new PasswordViewFragment();
        fragment.setPassword(passwordItem);
        return fragment;
    }
    void setPassword(PasswordEntity pass) {
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
        getActivity().setTitle(password.getDomain_name());
        TextView tmp = view.findViewById(R.id.username_view);
        tmp.setText(password.getUsername());
        tmp = view.findViewById(R.id.url_view);
        tmp.setText(password.getURL());
        tmp = view.findViewById(R.id.password_view);
        tmp.setText(password.getPassword());

        view.findViewById(R.id.edit_button).setOnClickListener(v -> {
            ((MainActivity)getActivity()).navigateToFragment(EditFragment.newInstance(password), true);
        });
    }

    @Override
    public void onDestroyView() {
        getActivity().setTitle(getResources().getString(R.string.app_name));
        super.onDestroyView();
    }
}
