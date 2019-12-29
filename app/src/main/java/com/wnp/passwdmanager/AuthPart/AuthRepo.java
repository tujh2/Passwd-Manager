package com.wnp.passwdmanager.AuthPart;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wnp.passwdmanager.Network.ApiRepo;
import com.wnp.passwdmanager.Network.LoginApi;
import com.wnp.passwdmanager.RepoApplication;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepo {
    private final ApiRepo mApiRepo;

    public AuthRepo(ApiRepo apiRepo) {
        mApiRepo = apiRepo;
    }

    @NonNull
    static AuthRepo getInstance(Context context) {
        return RepoApplication.from(context).getmAuthRepo();
    }

    private String mCurrentUser;
    private MutableLiveData<AuthProgress> mAuthProgress;

    LiveData<AuthProgress> registrate(@NonNull String username, @NonNull String password) {
        if (TextUtils.equals(username, mCurrentUser) && mAuthProgress.getValue() == AuthProgress.IN_PROGRESS) {
            return mAuthProgress;
        } else if (!TextUtils.equals(username, mCurrentUser) && mAuthProgress != null) {
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
        LoginApi api = mApiRepo.getmAuthApi();
        String encryptionKey = generateKey();
        String passwordHash = getHash(getHash(password));
        String encryptedKey = encryptKey(password, encryptionKey);
        api.registarte(new LoginApi.RegUserPlain(username, passwordHash, encryptedKey)).enqueue(new Callback<LoginApi.Response>() {
            @Override
            public void onResponse(@NotNull Call<LoginApi.Response> call, @NotNull Response<LoginApi.Response> response) {
                Log.d("AuthRepo", "regonResponse" + response.code());
                if (response.isSuccessful() && response.body() != null && response.code() == 200) {
                    Log.d("AuthRepo", "REG " + response.body().token);
                    RepoApplication.setCurrentSyncNumber(0);
                    Log.d("AuthRepo", encryptionKey);
                    RepoApplication.setUsername(username);
                    RepoApplication.setPassword(passwordHash);
                    RepoApplication.setEncryptionKey(encryptionKey);
                    RepoApplication.setToken(response.body().token);
                    progress.postValue(AuthProgress.SUCCESS);
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

    LiveData<AuthProgress> login(@NonNull String username, @NonNull String password) {
        if (TextUtils.equals(username, mCurrentUser) && mAuthProgress.getValue() == AuthProgress.IN_PROGRESS) {
            return mAuthProgress;
        } else if (!TextUtils.equals(username, mCurrentUser) && mAuthProgress != null) {
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
                       @NonNull final String password) {
        Log.d("AuthRepo", "Started NetworkLogin");
        LoginApi api = mApiRepo.getmAuthApi();
        api.Auth(new LoginApi.UserPlain(username, getHash(getHash(password)))).enqueue(new Callback<LoginApi.Response>() {
            @Override
            public void onResponse(@NotNull Call<LoginApi.Response> call, @NotNull Response<LoginApi.Response> response) {
                Log.d("AuthRepo", "onResponse" + response.code());
                if (response.body() != null && response.code() == 200) {
                    progress.postValue(AuthProgress.SUCCESS);
                    RepoApplication.setCurrentSyncNumber(0);
                    RepoApplication.setToken(response.body().token);
                    RepoApplication.setEncryptionKey(decryptKey(response.body().encryptedKey, password));
                    RepoApplication.setUsername(username);
                    RepoApplication.setPassword(getHash(getHash(password)));
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

    private String getHash(String str){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(str.getBytes());
            return bytesToHex(encoded);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            return bytesToHex(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String encryptKey(String keyForEncrypt, String encryptionKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            SecretKeySpec sks = new SecretKeySpec(digest.digest(encryptionKey.getBytes()), "AES/CBC/PKCS5PADDING");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            byte[] encryptedKey = cipher.doFinal(keyForEncrypt.getBytes());
            return bytesToHex(encryptedKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decryptKey(String keyForDecrypt, String encryptionKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            SecretKeySpec sks = new SecretKeySpec(digest.digest(encryptionKey.getBytes()), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] decryptedKey = cipher.doFinal(keyForDecrypt.getBytes());
            return bytesToHex(decryptedKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String bytesToHex(byte[] bytes) {
        Log.d("AuthRepo", "encr key length " + bytes.length);
        StringBuffer hexString = new StringBuffer();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    enum AuthProgress {
        IN_PROGRESS,
        SUCCESS,
        FAILED
    }
}
