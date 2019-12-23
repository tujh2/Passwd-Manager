package com.wnp.passwdmanager.Network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.wnp.passwdmanager.RepoApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class SyncWorker extends Worker {
    private static final String TAG = "syncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            RepoApplication application = RepoApplication.from(getApplicationContext());
            application.getPasswordsRepository().close(getApplicationContext());

            Response<GetPostDbApi.syncNumResponse> numResponse = application.getmApi()
                    .getDatabaseApi()
                    .getSyncNumber("Bearer " + RepoApplication.getToken())
                    .execute();
            if (numResponse.code() == 401) {
                Response<LoginApi.Response> tokenResponse = application.getmApi().getmAuthApi()
                        .Auth(new LoginApi.UserPlain(RepoApplication.getUsername(),
                                RepoApplication.getPassword())).execute();
                if (tokenResponse.code() == 200 && tokenResponse.isSuccessful() && tokenResponse.body() != null) {
                    RepoApplication.setToken(tokenResponse.body().token);
                } else return Result.failure();
                return Result.retry();
            }
            //Log.d(TAG, "" + RepoApplication.getCurrentSyncNumber() + " " + numResponse.body().syncNumber);
            if (numResponse.body() != null && numResponse.isSuccessful()) {
                int currentSyncNumber = RepoApplication.getCurrentSyncNumber();
                if (currentSyncNumber < numResponse.body().syncNumber) {
                    Response<ResponseBody> response = RepoApplication.from(getApplicationContext()).getmApi().getDatabaseApi()
                            .getPasswordsDatabase("Bearer " + RepoApplication.getToken()).execute();
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        if (writeResponseBodyToDisk(response.body())) {
                            Log.d(TAG, "db has been received");
                            RepoApplication.setCurrentSyncNumber(numResponse.body().syncNumber);
                            return Result.success();
                        }
                    }
                } else if (currentSyncNumber > numResponse.body().syncNumber) {
                    File file = new File(getApplicationContext().getDatabasePath("userPasswords.db").getAbsolutePath());
                    RequestBody requestFile = RequestBody.create(file, MediaType.parse("*/*"));
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    RequestBody filename = RequestBody.create(file.getName(), MediaType.parse("text/plain"));
                    Response<GetPostDbApi.ResponseOnPush> responseBody = RepoApplication.from(getApplicationContext()).getmApi()
                            .getDatabaseApi()
                            .pushPasswordsDatabase("Bearer " + RepoApplication.getToken(), body, filename).execute();
                    if (responseBody.isSuccessful() && responseBody.body() != null) {
                        if (responseBody.body().status.equals("uploaded")) {
                            Log.d(TAG, "pushToServer success");
                            return Result.success();
                        }
                    }
                } else return Result.success();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.failure();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            //File file = new File(getFilesDir().getAbsolutePath() + File.separator + "encryptedDb");
            File file = new File(getApplicationContext().getDatabasePath("userPasswords.db").getAbsolutePath());
            InputStream inputStream = null;
            OutputStream outputStream = null;
            if (file.delete())
                file.createNewFile();
            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("file", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
