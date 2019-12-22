package com.wnp.passwdmanager.AuthPart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wnp.passwdmanager.R;
import com.wnp.passwdmanager.RepoApplication;

public class UnlockFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_unlcok_fragment, container, false);
        EditText pass = view.findViewById(R.id.unlock_pass);
        view.findViewById(R.id.unlock_button).setOnClickListener(v -> {
            if(RepoApplication.getPin().equals(pass.getText().toString()) ) {
                AuthActivity authActivity = (AuthActivity)getActivity();
                if(authActivity != null)
                    authActivity.switchActivity();
            }
            else
                Toast.makeText(getContext(), R.string.wrong_pin, Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}
