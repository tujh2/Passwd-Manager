package com.wnp.passwdmanager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.Header;

public class NetworkManager {
    public static final MediaType POSTPARAM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    //public static final String SERVER = "https://1611b.l.time4vps.cloud";
    public static final String SERVER = "http://192.168.31.114";
    private static final String TAG = "NETWORK";
    private static final NetworkManager INSTANCE = new NetworkManager();
    private final OkHttpClient client = new OkHttpClient();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private volatile OnRequestCompleteListener mListener;

    private NetworkManager() {}

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    public void post(final String url, final JSONObject json, final OnRequestCompleteListener listener) {
        addListener(listener);
        RequestBody postBody = RequestBody.create(json.toString(), JSON);
        final Request request = new Request.Builder()
                .url(url)
                .post(postBody)
                .build();
        performRequest(request);
    }

    void get(final String url, final String token, final OnRequestCompleteListener listener) {
        addListener(listener);
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", token)
                .build();
        performRequest(request);
    }

    private void performRequest(final Request request) {
        executor.execute(() -> {
            final JSONObject body = getBody(request);
            if(mListener != null)
                mListener.onRequestComplete(body);
        });
    }

    private JSONObject getBody(Request request) {
        try {
            final Response response = client.newCall(request).execute();
            try (ResponseBody body = response.body()){
                if(response.isSuccessful() && body != null)
                    return new JSONObject(body.string());
                else
                    return  new JSONObject("{\"code\": " + "\"" + response.code() + "\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e(TAG, "Fail to perform request", e);
        }
        return null;
    }

    public void addListener(OnRequestCompleteListener listener) {
        mListener = listener;
    }

    void clear() {
        mListener = null;
    }

    public interface OnRequestCompleteListener {
        void onRequestComplete(final JSONObject body);
    }
}
