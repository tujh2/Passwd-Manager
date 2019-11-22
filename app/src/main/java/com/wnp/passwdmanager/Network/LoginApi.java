package com.wnp.passwdmanager.Network;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {
    class UserPlain {
        public UserPlain(@NonNull String name, @NonNull String pass) {
            username = name; password = pass;
        }
        final String username;
        final String password;
    }
    class Response {
        public String code;
        public String expire;
        public String token;
    }

    @POST("/login")
    Call<Response> Auth(@Body UserPlain body);
}
