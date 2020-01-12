package com.wnp.passwdmanager.AuthPart;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.wnp.passwdmanager.R;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private UserInfoViewModel loginModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_signin_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(UserInfoViewModel.class);
        EditText user = view.findViewById(R.id.username);
        EditText pass = view.findViewById(R.id.password);
        Button loginBtm = view.findViewById(R.id.login_button);
        AlertDialog alertError = new AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setCancelable(false)
                .setNegativeButton(R.string.OK, (dialog, which) -> dialog.cancel())
                .setMessage(R.string.errorLoginMsg)
                .create();

        AlertDialog alertFailed = new AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setCancelable(false)
                .setNegativeButton(R.string.OK, (dialog, which) -> dialog.cancel())
                .setMessage(R.string.failedLoginMsg).create();

        loginModel.getProgress().observe(getViewLifecycleOwner(), userState -> {
            switch (userState) {
                case LOGIN_FAILED:
                    loginBtm.setEnabled(true);
                    alertFailed.show();
                    break;
                case LOGIN_ERROR:
                    loginBtm.setEnabled(true);
                    alertError.show();
                    break;
                case LOGIN_IN_PROGRESS:
                    loginBtm.setEnabled(false);
                    break;
                case LOGIN_SUCCESS:
                    Toast.makeText(getContext(), R.string.successLogin, Toast.LENGTH_SHORT).show();
                    ((AuthActivity)getActivity()).switchActivity();
                    break;

                default: loginBtm.setEnabled(true); break;

            }
        });

        view.findViewById(R.id.login_button).setOnClickListener(v -> loginModel.login(user.getText().toString(), pass.getText().toString()));
        view.findViewById(R.id.reg_switch_but).setOnClickListener(v ->
                ((AuthActivity)getActivity()).navigateToFragment(RegisterFragment.newInstance(), true));
    }
}
