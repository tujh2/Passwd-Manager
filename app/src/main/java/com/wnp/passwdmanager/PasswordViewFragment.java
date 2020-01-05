package com.wnp.passwdmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wnp.passwdmanager.Database.PasswordEntity;

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
        if(getArguments() != null)
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
        
        if(getArguments() != null) {
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
        tmp.setText(password.getUsername());
        tmp = view.findViewById(R.id.url_view);
        tmp.setText(password.getURL());
        tmp = view.findViewById(R.id.password_view);
        tmp.setText(password.getPassword());

        view.findViewById(R.id.edit_button).setOnClickListener(v ->
                ((MainActivity)getActivity()).navigateToFragment(EditFragment.newInstance(password), "EDIT"));
    }

    @Override
    public void onDestroyView() {
        Objects.requireNonNull(getActivity()).setTitle(getResources().getString(R.string.app_name));
        super.onDestroyView();
    }
}
