package com.wnp.passwdmanager.AuthPart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.wnp.passwdmanager.MainActivity;
import com.wnp.passwdmanager.MainFragment;
import com.wnp.passwdmanager.R;
import com.wnp.passwdmanager.RepoApplication;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DefaultSettingsFragment extends Fragment {
    private final static String TAG = "DefaultSettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.default_settings_fragment, container, false);
        EditText pinText = view.findViewById(R.id.pin);
        Switch fingerprintSwitch = view.findViewById(R.id.fingerprint_switch);
        MutableLiveData<Boolean> fingerprintStateLiveData = new MutableLiveData<>();
        fingerprintStateLiveData.observe(getViewLifecycleOwner(),
                fingerprintSwitch::setChecked);

        if (BiometricManager.from(RepoApplication.getApplication()).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
            fingerprintSwitch.setVisibility(View.VISIBLE);
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

        view.findViewById(R.id.finish_but).setOnClickListener(v -> {
            RepoApplication.setDefaultPin(pinText.getText().toString());
            ((MainActivity) Objects.requireNonNull(getActivity()))
                    .navigateToFragment(new MainFragment(), null);
            //((AuthActivity) Objects.requireNonNull(getActivity())).switchActivity();
        });
        return view;
    }
}
