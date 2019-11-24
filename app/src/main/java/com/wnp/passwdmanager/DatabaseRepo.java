package com.wnp.passwdmanager;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wnp.passwdmanager.Network.ApiRepo;
import com.wnp.passwdmanager.Network.DatabaseApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseRepo {
    private ApiRepo mApiRepo;
    private static String dbName;
    private MutableLiveData<DatabaseStatus> mDatabaseStatus;
    private String token;

    DatabaseRepo(ApiRepo apiRepo) {
        mApiRepo = apiRepo;
    }

    static DatabaseRepo getInstance(Context context) {
        dbName = context.getFilesDir().getAbsolutePath() + "/userPasswords.db";
        return RepoApplication.from(context).getDatabaseRepo();
    }

    LiveData<DatabaseStatus> getDatabase() {
        mDatabaseStatus = new MutableLiveData<>();
        mDatabaseStatus.postValue(DatabaseStatus.IN_PROGRESS);
        requestDatabase(mDatabaseStatus);
        return mDatabaseStatus;
    }

    private void requestDatabase(final MutableLiveData<DatabaseStatus> progress) {
        DatabaseApi databaseApi = mApiRepo.getDatabaseApi();
        databaseApi.getPasswordsDatabase("Bearer " + RepoApplication.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("NetworkDB", "code "+ response.code());
                if(response.isSuccessful()) {
                    if( writeDbToInternalStorage(response.body()) ) {
                        Log.d("NetworkDB","DB Written");
                        progress.postValue(DatabaseStatus.SUCCESS);
                        return;
                    }
                }
                progress.postValue(DatabaseStatus.FAILED);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("NetworkDB", "failure");
                progress.postValue(DatabaseStatus.FAILED);
            }
        });
    }

    private boolean writeDbToInternalStorage(ResponseBody body) {
        try {
            File dbFile = new File(dbName);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(dbFile);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if(read == -1 ) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if(inputStream != null) {
                    inputStream.close();
                }
                if(outputStream != null) {
                    outputStream.close();
                }
            }

        } catch (IOException e) {
            return false;
        }
    }
    enum DatabaseStatus {
        SUCCESS,
        IN_PROGRESS,
        FAILED
    }
}
