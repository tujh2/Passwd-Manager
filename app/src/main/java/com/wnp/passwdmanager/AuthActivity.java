package com.wnp.passwdmanager;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {
    private final NetworkManager.OnRequestCompleteListener listener =
            body -> runOnUiThread(() -> Toast.makeText(AuthActivity.this.getApplicationContext(), body, Toast.LENGTH_SHORT).show());

    private EditText user;
    private EditText pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);

        findViewById(R.id.login_button).setOnClickListener(v -> {
            NetworkManager.getInstance().get(NetworkManager.SERVER,
                    user.getText().toString() + ":" + pass.getText().toString(), listener);
        });
        NetworkManager.getInstance().addListener(listener);
    }

    @Override
    protected void onDestroy() {
        NetworkManager.getInstance().clear();
        super.onDestroy();
    }
}
