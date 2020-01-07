package com.wnp.passwdmanager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wnp.passwdmanager.Database.PasswordEntity;

import org.w3c.dom.Text;

import java.util.Objects;

public class PasswordViewFragment extends Fragment {
    private static final String ITEM = "ITEM";
    private PasswordEntity password;
    private PasswordsViewModel passwordsViewModel;

    static PasswordViewFragment newInstance(PasswordEntity passwordItem) {
        PasswordViewFragment fragment = new PasswordViewFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ITEM, passwordItem);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_record_option) {
            passwordsViewModel.delete(password);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStop() {
        if (getArguments() != null)
            getArguments().putSerializable(ITEM, password);
        super.onStop();
    }

    private void setPassword(PasswordEntity pass) {
        password = pass;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        passwordsViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(PasswordsViewModel.class);

        if (getArguments() != null) {
            password = (PasswordEntity) getArguments().getSerializable(ITEM);
            setHasOptionsMenu(true);
        }
        return inflater.inflate(R.layout.password_view_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle(password.getDomain_name());
        TextView tmp = view.findViewById(R.id.username_view);
        TextView passwordView = view.findViewById(R.id.password_view);
        tmp.setText(password.getUsername());
        tmp = view.findViewById(R.id.url_view);
        tmp.setText(password.getURL());
        String pass = password.getPassword();
        String filledString = fillString(pass.length());
        passwordView.setText(filledString);

        ClipboardManager clipboardManager = (ClipboardManager) getActivity()
                .getSystemService(Context.CLIPBOARD_SERVICE);

        view.findViewById(R.id.edit_button).setOnClickListener(v ->
                ((MainActivity) getActivity()).navigateToFragment(EditFragment.newInstance(password), "EDIT"));
        view.findViewById(R.id.copy_username_but).setOnClickListener(v -> {
            ClipData clipData = ClipData.newPlainText(null, password.getUsername());
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.copy_url_but).setOnClickListener(v -> {
            ClipData clipData = ClipData.newPlainText(null, password.getURL());
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });

        view.findViewById(R.id.copy_password_but).setOnClickListener(v -> {
            ClipData clipData = ClipData.newPlainText(null, password.getPassword());
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), R.string.copied, Toast.LENGTH_SHORT).show();
            }
        });
        ToggleButton toggleButton = view.findViewById(R.id.toggle_visibility_button);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                passwordView.setText(pass);
            } else {
                passwordView.setText(filledString);
            }
        });
    }

    @Override
    public void onDestroyView() {
        Objects.requireNonNull(getActivity()).setTitle(getResources().getString(R.string.app_name));
        super.onDestroyView();
    }

    private String fillString(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        while (stringBuilder.length() != count)
            stringBuilder.append('*');
        return stringBuilder.toString();
    }
}
