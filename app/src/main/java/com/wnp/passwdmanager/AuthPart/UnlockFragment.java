package com.wnp.passwdmanager.AuthPart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import com.wnp.passwdmanager.MainActivity;
import com.wnp.passwdmanager.MainFragment;
import com.wnp.passwdmanager.R;
import com.wnp.passwdmanager.RepoApplication;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UnlockFragment extends Fragment {
    private static final String TAG = "UnlockFragment";

    @Override
    public void onResume() {
        super.onResume();
        if( BiometricManager.from(RepoApplication.getApplication()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
                && RepoApplication.isFingerPrintEnabled()) {
            Executor executor = Executors.newSingleThreadExecutor();
            final BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    onSuccessPin();
                    Log.d(TAG, "FINGERPRINT SUCCESS");
                }
            };
            final BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, callback);
            final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.fingerprint_title))
                    .setNegativeButtonText(getString(R.string.negative_button))
                    .build();

            biometricPrompt.authenticate(promptInfo);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_unlcok_fragment, container, false);
        Button unlockButton = view.findViewById(R.id.unlock_button);
        EditText pass = view.findViewById(R.id.unlock_pass);
        unlockButton.setOnClickListener(v -> {
            if (RepoApplication.getPin().equals(pass.getText().toString())) {
                onSuccessPin();
            } else
                Toast.makeText(getContext(), R.string.wrong_pin, Toast.LENGTH_SHORT).show();
        });
        return view;
    }

    private void onSuccessPin() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.navigateToFragment(new MainFragment(), null);
        }
    }
}
