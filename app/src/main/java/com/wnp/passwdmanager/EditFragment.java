package com.wnp.passwdmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Database;

import com.wnp.passwdmanager.Database.DatabaseManager;

public class EditFragment extends Fragment {
    private PasswordsRepository passRepo;

    static EditFragment newInstance(PasswordsRepository passwordsRepository) {
        EditFragment fragment = new EditFragment();
        fragment.passRepo = passwordsRepository;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_edit_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText domain = view.findViewById(R.id.domain_edit);
        EditText url = view.findViewById(R.id.url_edit);
        EditText username = view.findViewById(R.id.username_edit);
        EditText password = view.findViewById(R.id.password_edit);

        view.findViewById(R.id.save_button).setOnClickListener( v -> {
            DatabaseManager.Item  item = new DatabaseManager.Item();
            item.domain = domain.getText().toString();
            item.url = url.getText().toString();
            item.username = username.getText().toString();
            item.password = password.getText().toString();
            DatabaseManager.getInstance(getContext()).insert(item);
            //passRepo.addItem(item);
            super.onDestroy();
        });

    }
}
