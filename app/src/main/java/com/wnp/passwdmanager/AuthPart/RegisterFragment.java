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

@SuppressWarnings("WeakerAccess")
public class RegisterFragment extends Fragment {
    private UserInfoViewModel regViewModel;

    static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_register_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText user = view.findViewById(R.id.login_reg);
        EditText pass = view.findViewById(R.id.password_reg);
        Button regBtn = view.findViewById(R.id.reg_button);
        regViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity()))
                .get(UserInfoViewModel.class);
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
                .setMessage(R.string.failedRegMsg).create();

        regViewModel.getProgress().observe(getViewLifecycleOwner(), userState -> {
            switch (userState) {
                case REG_FAILED:
                    regBtn.setEnabled(true);
                    alertFailed.show();
                    break;
                case REG_ERROR:
                    regBtn.setEnabled(true);
                    alertError.show();
                    break;
                case REG_IN_PROGRESS:
                    regBtn.setEnabled(false);
                    break;
                case REG_SUCCESS:
                    Toast.makeText(getContext(), R.string.successReg, Toast.LENGTH_SHORT).show();
                    ((AuthActivity) getActivity()).switchActivity();
                    break;

                default:
                    regBtn.setEnabled(true);
                    break;
            }
        });

        regBtn.setOnClickListener(v ->
                regViewModel.registrate(user.getText().toString(), pass.getText().toString()));
    }
}
