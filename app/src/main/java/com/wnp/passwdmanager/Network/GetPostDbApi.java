package com.wnp.passwdmanager.Network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface GetPostDbApi {

    class syncNumResponse {
        syncNumResponse(int num) {
            syncNumber = num;
        }
        int syncNumber;
    }

    @GET("/auth/getSyncNumber")
    Call<syncNumResponse> getSyncNumber(@Header("Authorization") String token);

    @POST("/auth/setSyncNumber")
    Call<ResponseBody> setSyncNumber(@Header("Authorization") String token, @Body syncNumResponse sendBody);

    @GET("/auth/getDatabase")
    Call<ResponseBody> getPasswordsDatabase(@Header("Authorization") String token);

    class ResponseOnPush {
        String status;
    }

    @Multipart
    @POST("/auth/pushDatabase")
    Call<ResponseOnPush> pushPasswordsDatabase(@Header("Authorization") String token,
                                               @Part MultipartBody.Part file,
                                               @Part("file") RequestBody name);
}
