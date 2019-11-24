package com.wnp.passwdmanager.Network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;

public interface DatabaseApi {

    @GET("/auth/getDatabase")
    Call<ResponseBody> getPasswordsDatabase(@Header("Authorization") String token);
}
