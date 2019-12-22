package com.wnp.passwdmanager.AuthPart;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wnp.passwdmanager.Network.ApiRepo;
import com.wnp.passwdmanager.Network.LoginApi;
import com.wnp.passwdmanager.Network.RegApi;
import com.wnp.passwdmanager.RepoApplication;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepo {
    private final ApiRepo mApiRepo;

    public AuthRepo(ApiRepo apiRepo) {
        mApiRepo = apiRepo;
    }

    @NonNull
    public static AuthRepo getInstance(Context context) {
        return RepoApplication.from(context).getmAuthRepo();
    }

    private String mCurrentUser;
    private MutableLiveData<AuthProgress> mAuthProgress;

    LiveData<AuthProgress> registrate(@NonNull String username, @NonNull String password) {
        if(TextUtils.equals(username, mCurrentUser) && mAuthProgress.getValue() == AuthProgress.IN_PROGRESS) {
            return mAuthProgress;
        } else if(!TextUtils.equals(username, mCurrentUser) && mAuthProgress != null) {
            mAuthProgress.postValue(AuthProgress.FAILED);
        }
        mCurrentUser = username;
        mAuthProgress = new MutableLiveData<>();
        mAuthProgress.postValue(AuthProgress.IN_PROGRESS);
        requestReg(mAuthProgress, username, password);
        return mAuthProgress;
    }

    private void requestReg(final MutableLiveData<AuthProgress> progress,
                            @NonNull final String username,
                            @NonNull final String password) {
        Log.d("AuthRepo", "Started NetworkLogin");
        RegApi api = mApiRepo.getmRegApi();
        api.Registration(new RegApi.UserPlain(username, password)).enqueue(new Callback<RegApi.Status>() {
            @Override
            public void onResponse(@NotNull Call<RegApi.Status> call, @NotNull Response<RegApi.Status> response) {
                Log.d("AuthRepo", "regonResponse" + response.code());
                if(response.isSuccessful() && response.body() != null) {
                    Log.d("AuthRepo",  response.body().regStatus);
                    if(response.body().regStatus.equals("successfully")) {
                        RepoApplication.setCurrentSyncNumber(0);
                        progress.postValue(AuthProgress.SUCCESS);
                    }
                    else
                        progress.postValue(AuthProgress.FAILED);
                } else
                    progress.postValue(AuthProgress.FAILED);
            }

            @Override
            public void onFailure(@NotNull Call<RegApi.Status> call, @NotNull Throwable t) {
                Log.d("AuthRepo", "onFailure" + t.toString());
                progress.postValue(AuthProgress.FAILED);
            }
        });

    }

    LiveData<AuthProgress> login(@NonNull String username, @NonNull String password) {
        if(TextUtils.equals(username, mCurrentUser) && mAuthProgress.getValue() == AuthProgress.IN_PROGRESS) {
            return mAuthProgress;
        } else if(!TextUtils.equals(username, mCurrentUser) && mAuthProgress != null) {
            mAuthProgress.postValue(AuthProgress.FAILED);
        }
        mCurrentUser = username;
        mAuthProgress = new MutableLiveData<>();
        mAuthProgress.postValue(AuthProgress.IN_PROGRESS);
        login(mAuthProgress, username, password);
        return mAuthProgress;
    }

    private void login(final MutableLiveData<AuthProgress> progress,
                  @NonNull final String username,
                  @NonNull  final String password) {
        Log.d("AuthRepo", "Started NetworkLogin");
        LoginApi api = mApiRepo.getmAuthApi();
        api.Auth(new LoginApi.UserPlain(username, password)).enqueue(new Callback<LoginApi.Response>() {
            @Override
            public void onResponse(@NotNull Call<LoginApi.Response> call, @NotNull Response<LoginApi.Response> response) {
                Log.d("AuthRepo", "onResponse" + response.code());
                if(response.body() != null && response.code() == 200) {
                    progress.postValue(AuthProgress.SUCCESS);
                    RepoApplication.setCurrentSyncNumber(0);
                    RepoApplication.setToken(response.body().token);
                } else
                    progress.postValue(AuthProgress.FAILED);
            }

            @Override
            public void onFailure(@NotNull Call<LoginApi.Response> call, @NotNull Throwable t) {
                Log.d("AuthRepo", "onFailure" + t.toString());
                progress.postValue(AuthProgress.FAILED);
            }
        });
    }


    enum AuthProgress {
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }
}
