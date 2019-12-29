package com.wnp.passwdmanager.Network;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ApiRepo {
    private final LoginApi mAuthApi;
    private final GetPostDbApi mDatabaseApi;

    public ApiRepo() {
        OkHttpClient mOkHttpClient = new OkHttpClient()
                .newBuilder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(new HttpUrl.Builder().scheme("https")
                        .host("1611b.l.time4vps.cloud")
                        .build())
                .client(mOkHttpClient)
                .build();
        mAuthApi = retrofit.create(LoginApi.class);
        mDatabaseApi = retrofit.create(GetPostDbApi.class);
    }

    public LoginApi getmAuthApi() { return mAuthApi; }
    public GetPostDbApi getDatabaseApi() { return mDatabaseApi; }
}
