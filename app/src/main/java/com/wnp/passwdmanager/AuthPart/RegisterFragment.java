package com.wnp.passwdmanager.AuthPart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wnp.passwdmanager.NetworkManager;
import com.wnp.passwdmanager.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterFragment extends Fragment {
    private final JSONObject userInfo = new JSONObject();
    private final NetworkManager.OnRequestCompleteListener listener = AuthActivity.getInstance().listener;
    static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_register_fragment, container, false);
        EditText user  = view.findViewById(R.id.login_reg);
        EditText pass = view.findViewById(R.id.password_reg);
        view.findViewById(R.id.reg_button).setOnClickListener(v -> {
            try {
                userInfo.put("user", user.getText().toString())
                        .put("password", pass.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            NetworkManager.getInstance().post(NetworkManager.SERVER +"/reg", userInfo, listener);
        });
        return view;
    }
}
