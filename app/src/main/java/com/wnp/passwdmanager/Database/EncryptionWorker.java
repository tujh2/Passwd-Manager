package com.wnp.passwdmanager.Database;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.wnp.passwdmanager.RepoApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionWorker extends Worker {

    public static final String ENCRYPT = "ENCRYPT";
    public static final String DECRYPT = "DECRYPT";
    public static final String TYPE = "WORKER_TYPE";
    public static final String TAG = "ENCRYPTION";

    public EncryptionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        RepoApplication application = RepoApplication.from(getApplicationContext());
        application.getPasswordsRepository().close();

        String file = getApplicationContext()
                .getDatabasePath("userPasswords.db").getAbsolutePath();
        String encrDB = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "encryptedDB";
        String encryptionKey = RepoApplication.getEncryptionKey();
        Log.d(TAG, encryptionKey + " " + encryptionKey.getBytes().length);
        String inputData = getInputData().getString(TYPE);
        if (inputData != null) {
            if (inputData.equals(ENCRYPT)) {
                try {
                    encryptDb(file, encrDB, encryptionKey);
                    Log.d(TAG, "encrypted");
                    return Result.success();
                } catch (IOException |
                        NoSuchAlgorithmException |
                        NoSuchPaddingException |
                        InvalidKeyException e) {
                    e.printStackTrace();
                }
            } else if (inputData.equals(DECRYPT)) {
                try {
                    decryptDb(encrDB, file, encryptionKey);
                    Log.d(TAG, "decrypted");
                    return Result.success();
                } catch (IOException |
                        NoSuchAlgorithmException |
                        NoSuchPaddingException |
                        InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.failure();
    }

    private static void encryptDb(String file, String fileEncrypted, String key)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(fileEncrypted);
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
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
        f.delete();
    }

    private static void decryptDb(String fileEncrypted, String fileDecrypted, String key)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        FileInputStream fis = new FileInputStream(fileEncrypted);
        FileOutputStream fos = new FileOutputStream(fileDecrypted);
        SecretKeySpec sks = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
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
