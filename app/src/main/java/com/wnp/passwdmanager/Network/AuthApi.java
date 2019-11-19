package com.wnp.passwdmanager.Network;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    class UserPlain {
        public UserPlain(@NonNull String name, @NonNull String pass) {
            user = name; password = pass;
        }
        final String user;
        final String password;
    }
    class Status {
        public String status;
    }

    @POST("/auth")
    Call<Status> Auth(@Body UserPlain body);
}
