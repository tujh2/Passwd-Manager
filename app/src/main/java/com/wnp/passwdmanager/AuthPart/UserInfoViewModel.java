package com.wnp.passwdmanager.AuthPart;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.Objects;

public class UserInfoViewModel extends AndroidViewModel  {

    //LoginData mLastLoginData = new LoginData("", "");
    private final MediatorLiveData<UserState> mUserState = new MediatorLiveData<>();
    public LiveData<UserState> getProgress() { return  mUserState; }

    public UserInfoViewModel(@NonNull Application application) {
        super(application);
        mUserState.setValue(UserState.NONE);
    }

    void registrate(String login, String password) {
        LoginData regData = new LoginData(login, password);
        if(!regData.isValid()) {
            mUserState.postValue(UserState.REG_ERROR);
        } else if(mUserState.getValue() != UserState.REG_IN_PROGRESS) {
            requestRegistration(regData);
            requestLogin(regData);
        }
    }

    private void requestRegistration(LoginData regData) {
        mUserState.postValue(UserState.REG_IN_PROGRESS);
        final LiveData<AuthRepo.AuthProgress> progressLiveData = AuthRepo.getInstance(getApplication())
                .registrate(regData.getLogin(), regData.getPassword());
        mUserState.addSource(progressLiveData, authProgress -> {
            switch (authProgress) {
                case SUCCESS:
                    mUserState.postValue(UserState.REG_SUCCESS);
                    mUserState.removeSource(progressLiveData);
                    break;
                case FAILED:
                    mUserState.postValue(UserState.REG_FAILED);
                    mUserState.removeSource(progressLiveData);
                    break;
                case IN_PROGRESS:
                    mUserState.postValue(UserState.REG_IN_PROGRESS);
                    break;
                    default:
                        mUserState.postValue(UserState.REG_ERROR);
                        mUserState.removeSource(progressLiveData);
                        break;
            }
        });
    }

    void login(String login, String password) {
        //LoginData last = mLastLoginData;
        LoginData loginData = new LoginData(login, password);
        //mLastLoginData = loginData;

        if(!loginData.isValid()) {
            mUserState.postValue(UserState.LOGIN_ERROR);
        } // else if (last != null && last.equals(loginData)) {
          //  Log.w("LoginViewModel", "Ignoring duplicate request with login data");
        else if (mUserState.getValue() != UserState.LOGIN_IN_PROGRESS) {
            requestLogin(loginData);
        }
    }

    private void requestLogin(final LoginData loginData) {
        mUserState.postValue(UserState.LOGIN_IN_PROGRESS);
        final LiveData<AuthRepo.AuthProgress> progressLiveData = AuthRepo.getInstance(getApplication())
                .login(loginData.getLogin(), loginData.getPassword());
        mUserState.addSource(progressLiveData, authProgress -> {
            switch (authProgress) {
                case SUCCESS:
                    mUserState.postValue(UserState.LOGIN_SUCCESS);
                    mUserState.removeSource(progressLiveData);
                    Log.d("UserInfoViewModel",  "LOGSUCCESS");
                    break;
                case FAILED:
                    mUserState.postValue(UserState.LOGIN_FAILED);
                    mUserState.removeSource(progressLiveData);
                    Log.d("UserInfoViewModel",  "LOGFAIL");
                    break;
                case IN_PROGRESS:
                    mUserState.postValue(UserState.LOGIN_IN_PROGRESS);
                    Log.d("UserInfoViewModel",  "LOGINPROG");
                    break;
                default:
                    mUserState.postValue(UserState.LOGIN_ERROR);
                    Log.d("UserInfoViewModel",  "LOGERROR");
                    mUserState.removeSource(progressLiveData);
                    break;
            }
        });
    }

    enum UserState {
        NONE,
        LOGIN_ERROR,
        LOGIN_IN_PROGRESS,
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        REG_ERROR,
        REG_IN_PROGRESS,
        REG_SUCCESS,
        REG_FAILED
    }

    static class LoginData {
        private final String mLogin;
        private final String mPassword;

        LoginData(String login, String password) { mLogin = login; mPassword = password; }

        String getLogin() { return mLogin; }
        String getPassword() { return mPassword; }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        boolean isValid() {
            return !TextUtils.isEmpty(mLogin) && !TextUtils.isEmpty(mPassword);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mLogin, mPassword);
        }
    }
}
