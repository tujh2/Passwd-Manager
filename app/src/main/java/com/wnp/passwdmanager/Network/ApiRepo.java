package com.wnp.passwdmanager.Network;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ApiRepo {
    private final OkHttpClient mOkHttpClient;
    private final LoginApi mAuthApi;
    private final RegApi mRegApi;
    private final GetDatabaseApi mGetDatabaseApi;

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
        mAuthApi = retrofit.create(LoginApi.class);
        mRegApi = retrofit.create(RegApi.class);
        mGetDatabaseApi = retrofit.create(GetDatabaseApi.class);
    }

    public LoginApi getmAuthApi() { return mAuthApi; }
    public RegApi getmRegApi() { return mRegApi; }
    public GetDatabaseApi getDatabaseApi() { return mGetDatabaseApi; }
}
