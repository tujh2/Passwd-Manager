package com.wnp.passwdmanager.Network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GetPostDbApi {

    @GET("/auth/getDatabase")
    Call<ResponseBody> getPasswordsDatabase(@Header("Authorization") String token);

    @Multipart
    @POST("/auth/pushDatabase")
    Call<ResponseBody> pushPasswordsDatabase(@Header("Authorization") String token,
                                             @Part MultipartBody.Part file);
}
