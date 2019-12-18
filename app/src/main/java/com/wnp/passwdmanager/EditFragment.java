package com.wnp.passwdmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.wnp.passwdmanager.Database.PasswordsRepository;
import com.wnp.passwdmanager.Database.PasswordEntity;

public class EditFragment extends Fragment {
    private PasswordsViewModel passwordsViewModel;

    static EditFragment newInstance() {
        return new EditFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.password_edit_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        passwordsViewModel = new ViewModelProvider(getActivity()).get(PasswordsViewModel.class);

        EditText domain = view.findViewById(R.id.domain_edit);
        EditText url = view.findViewById(R.id.url_edit);
        EditText username = view.findViewById(R.id.username_edit);
        EditText password = view.findViewById(R.id.password_edit);

        view.findViewById(R.id.save_button).setOnClickListener( v -> {
            PasswordEntity item = new PasswordEntity(domain.getText().toString(),
                    url.getText().toString(),
                    username.getText().toString(),
                    password.getText().toString());
            passwordsViewModel.insert(item);
            getActivity().getSupportFragmentManager().popBackStack();
        });

    }
}
