package com.wnp.passwdmanager.Network;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegApi {
    class UserPlain {
        public UserPlain(@NonNull String name, @NonNull String pass) {
            username = name; password = pass;
        }
        final String username;
        final String password;
    }
    class Status {
        public String regStatus;
    }

    @POST("/reg")
    Call<Status> Registration(@Body UserPlain body);

}
