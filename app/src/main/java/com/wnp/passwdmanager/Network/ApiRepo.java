package com.wnp.passwdmanager.Network;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ApiRepo {
    private final OkHttpClient mOkHttpClient;
    private final AuthApi mAuthApi;
    private final RegApi mRegApi;

    public ApiRepo() {
        mOkHttpClient = new OkHttpClient()
                .newBuilder()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(new HttpUrl.Builder().scheme("https")
                        .host("1611b.l.time4vps.cloud")
                        .build())
                .client(mOkHttpClient)
                .build();
        mAuthApi = retrofit.create(AuthApi.class);
        mRegApi = retrofit.create(RegApi.class);
    }

    public AuthApi getmAuthApi() { return mAuthApi; }
    public RegApi getmRegApi() { return mRegApi; }
}
