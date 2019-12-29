package com.wnp.passwdmanager.Network;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LoginApi {
    class UserPlain {
        public UserPlain(@NonNull String name, @NonNull String pass) {
            username = name; password = pass;
        }
        final String username;
        final String password;
    }
    class RegUserPlain {
        public RegUserPlain(@NonNull String name, @NonNull String pass, @NonNull String key) {
            username = name; password = pass; encryptedKey = key;
        }
        final String username;
        final String password;
        final String encryptedKey;
    }

    class Response {
        public String expire;
        public String token;
        public String encryptedKey;
    }

    class KeyResponse {
        public String encryptedKey;
    }

    @POST("/login")
    Call<Response> Auth(@Body UserPlain body);

    @POST("/login?action=reg")
    Call<Response> registarte(@Body RegUserPlain body);

    @GET("/getEncryptionKey")
    Call<KeyResponse> getEncryptionKey(@Header("Authorization") String token);
}
