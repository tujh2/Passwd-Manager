package com.wnp.passwdmanager.Network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.wnp.passwdmanager.Database.EncryptionWorker;
import com.wnp.passwdmanager.RepoApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
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
            application.getPasswordsRepository().close();

            String fileDecr = getApplicationContext()
                    .getDatabasePath("userPasswords.db").getAbsolutePath();
            String encrDB = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "encryptedDB";

            String token = RepoApplication.getToken();
            Response<GetPostDbApi.syncNumResponse> numResponse = application.getmApi()
                    .getDatabaseApi()
                    .getSyncNumber(token).execute();
            if (numResponse.code() == 401) {
                Response<LoginApi.Response> tokenResponse = application.getmApi().getmAuthApi()
                        .Auth(new LoginApi.UserPlain(RepoApplication.getUsername(),
                                RepoApplication.getPassword())).execute();
                if (tokenResponse.code() == 200 && tokenResponse.isSuccessful() && tokenResponse.body() != null) {
                    RepoApplication.setToken(tokenResponse.body().token);
                    token = tokenResponse.body().token;
                    numResponse = application.getmApi()
                            .getDatabaseApi()
                            .getSyncNumber(token).execute();
                    if(!numResponse.isSuccessful() && numResponse.code() != 200)
                        return Result.retry();
                } else return Result.failure();
            }
            //Log.d(TAG, "" + RepoApplication.getCurrentSyncNumber() + " " + numResponse.body().syncNumber);
            if (numResponse.body() != null && numResponse.isSuccessful()) {
                int currentSyncNumber = RepoApplication.getCurrentSyncNumber();
                if (currentSyncNumber < numResponse.body().syncNumber) {
                    Response<ResponseBody> response = application.getmApi().getDatabaseApi()
                            .getPasswordsDatabase(token).execute();
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        if (writeResponseBodyToDisk(response.body(), encrDB)) {
                            Log.d(TAG, "db has been received");
                            RepoApplication.setCurrentSyncNumber(numResponse.body().syncNumber);
                            decryptDb(encrDB, fileDecr, RepoApplication.getEncryptionKey());
                            return Result.success();
                        }
                    }
                } else if (currentSyncNumber > numResponse.body().syncNumber) {
                    encryptDb(fileDecr, encrDB, RepoApplication.getEncryptionKey());
                    File file = new File(encrDB);
                    RequestBody requestFile = RequestBody.create(file, MediaType.parse("*/*"));
                    MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    RequestBody filename = RequestBody.create(file.getName(), MediaType.parse("text/plain"));
                    Response<GetPostDbApi.ResponseOnPush> responseBody = application.getmApi()
                            .getDatabaseApi()
                            .pushPasswordsDatabase(token, body, filename).execute();
                    if (responseBody.isSuccessful() && responseBody.body() != null) {
                        if (responseBody.body().status.equals("uploaded")) {
                            GetPostDbApi.syncNumResponse sendNumber = new GetPostDbApi.syncNumResponse(currentSyncNumber);
                            Log.d(TAG, currentSyncNumber + " " + numResponse.body().syncNumber);
                            Response<ResponseBody> requestSetSyncNumber = application.getmApi()
                                     .getDatabaseApi().setSyncNumber(token, sendNumber).execute();
                            if(requestSetSyncNumber.isSuccessful()) {
                                Log.d(TAG, "pushToServer success");
                                return Result.success();
                            }
                        }
                    }
                } else return Result.success();
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return Result.failure();
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String filename) {
        try {
            //File file = new File(getFilesDir().getAbsolutePath() + File.separator + "encryptedDb");
            File file = new File(filename);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            //if (file.delete())
            //    file.createNewFile();
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

    private static void encryptDb(String file, String fileEncrypted, String key)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(fileEncrypted);
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec("asdfghjkqwertyui".getBytes()));
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        cos.flush();
        cos.close();
        fis.close();
        File f = new File(file);
    }

    private static void decryptDb(String fileEncrypted, String fileDecrypted, String key)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        FileInputStream fis = new FileInputStream(fileEncrypted);
        FileOutputStream fos = new FileOutputStream(fileDecrypted);
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec("asdfghjkqwertyui".getBytes()));
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }
}
