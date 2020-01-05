package com.wnp.passwdmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wnp.passwdmanager.Database.PasswordEntity;

import java.util.Objects;

public class EditFragment extends Fragment {
    private static final String ITEM = "ITEM";
    private PasswordsViewModel passwordsViewModel;
    private PasswordEntity passwordCurrent;

    static EditFragment newInstance(PasswordEntity passwordEntity) {
        EditFragment fragment = new EditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ITEM, passwordEntity);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments() != null) {
            setHasOptionsMenu(true);
            passwordCurrent = (PasswordEntity) getArguments().getSerializable(ITEM);
        }
        return inflater.inflate(R.layout.password_edit_fragment, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_record_option) {
            passwordsViewModel.delete(passwordCurrent);
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack("MAIN", 0);
            return true;
        }
        return false;
    }

    @Override
    public void onStop() {
        if(getArguments() != null)
            getArguments().putSerializable(ITEM, passwordCurrent);
        else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ITEM, passwordCurrent);
            setArguments(bundle);
        }
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        passwordsViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(PasswordsViewModel.class);

        EditText domain = view.findViewById(R.id.domain_edit);
        EditText url = view.findViewById(R.id.url_edit);
        EditText username = view.findViewById(R.id.username_edit);
        EditText password = view.findViewById(R.id.password_edit);

        if(passwordCurrent != null) {
            domain.setText(passwordCurrent.getDomain_name());
            url.setText(passwordCurrent.getURL());
            username.setText(passwordCurrent.getUsername());
            password.setText(passwordCurrent.getPassword());
        }

        view.findViewById(R.id.save_button).setOnClickListener( v -> {
            if(passwordCurrent == null) {
                passwordCurrent = new PasswordEntity(domain.getText().toString(),
                        url.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString());
                passwordsViewModel.insert(passwordCurrent);
            } else {
                passwordCurrent.setDomain_name(domain.getText().toString());
                passwordCurrent.setUsername(username.getText().toString());
                passwordCurrent.setURL(url.getText().toString());
                passwordCurrent.setPassword(password.getText().toString());
                passwordsViewModel.update(passwordCurrent);
            }

            getActivity().getSupportFragmentManager().popBackStack();
        });

    }
}
