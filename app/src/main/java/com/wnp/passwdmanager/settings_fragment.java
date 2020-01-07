package com.wnp.passwdmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class settings_fragment extends Fragment {
    private final static String TAG = "SettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.exit_button).setOnClickListener(v -> RepoApplication.getApplication().clearApplicationData());
        Switch fingerprintSwitch = view.findViewById(R.id.fingerprint_switch_settings);

        MutableLiveData<Boolean> fingerprintStateLiveData = new MutableLiveData<>();
        fingerprintStateLiveData.observe(getViewLifecycleOwner(),
                fingerprintSwitch::setChecked);

        if (BiometricManager.from(RepoApplication.getApplication()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            fingerprintSwitch.setVisibility(View.VISIBLE);
            fingerprintStateLiveData.postValue(RepoApplication.isFingerPrintEnabled());
        }

        fingerprintSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if(!RepoApplication.isFingerPrintEnabled()) {
                    Executor executor = Executors.newSingleThreadExecutor();
                    final BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            fingerprintStateLiveData.postValue(false);
                            Log.d(TAG, "FINGERPRINT ERROR");
                        }

                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            RepoApplication.setFingerprintStatus(true);
                            fingerprintStateLiveData.postValue(true);
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
            } else {
                RepoApplication.setFingerprintStatus(false);
            }
        });

        view.findViewById(R.id.change_pin_button).setOnClickListener(v -> {
            AlertDialog alertFailed = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error)
                    .setCancelable(false)
                    .setNegativeButton(R.string.OK, (dialog, which) -> dialog.cancel())
                    .setMessage(R.string.failedChangePin).create();

            @SuppressLint("InflateParams")
            View alertView = getLayoutInflater().inflate(R.layout.change_pin_layout, null);
            AlertDialog changePinAlert = new AlertDialog.Builder(getContext())
                    .setTitle("Changing pin")
                    .setView(alertView)
                    .setNegativeButton(R.string.negative_button, (dialog, which) -> dialog.cancel())
                    .setPositiveButton(R.string.OK, (dialog, which) -> {
                        EditText oldPin = alertView.findViewById(R.id.old_pin);
                        EditText newPin = alertView.findViewById(R.id.new_pin);
                        if(oldPin.getText().toString().equals(RepoApplication.getPin())) {
                            RepoApplication.setDefaultPin(newPin.getText().toString());
                            dialog.cancel();
                            Toast.makeText(getContext(), R.string.successPinChanged, Toast.LENGTH_SHORT).show();
                        } else {
                            alertFailed.show();
                        }
                    })
                    .create();
            changePinAlert.show();
        });
    }
}
