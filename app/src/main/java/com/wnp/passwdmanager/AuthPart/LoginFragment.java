package com.wnp.passwdmanager.AuthPart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wnp.passwdmanager.FragmentNavigator;
import com.wnp.passwdmanager.NetworkManager;
import com.wnp.passwdmanager.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {
    private final JSONObject userInfo = new JSONObject();
    private FragmentNavigator navigator;
    private final NetworkManager.OnRequestCompleteListener listener = AuthActivity.getInstance().listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(navigator == null)
            navigator = AuthActivity.getNavigator();
        View view =  inflater.inflate(R.layout.login_signin_fragment, container, false);
        EditText user = view.findViewById(R.id.username);
        EditText pass = view.findViewById(R.id.password);

        view.findViewById(R.id.login_button).setOnClickListener(v -> {
            try {
                userInfo.put("user", user.getText().toString())
                        .put("password", pass.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            NetworkManager.getInstance().post(NetworkManager.SERVER + "/auth", userInfo, listener);
        });
        view.findViewById(R.id.reg_switch_but).setOnClickListener(v -> navigator.navigateToFragment(RegisterFragment.newInstance(), true));
        return view;
    }
}
