package com.abbas.ali.serverfiletranfer.api;

import android.content.Context;

import com.abbas.ali.multipartrequest.MultipartRequest;
import com.abbas.ali.serverfiletranfer.singleton.RequestQueueSingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ApiService {

    /**
     * your server's base url is here
     */
    private static final String BASE_URL = "http://192.168.1.102:80/LaraAndroidTutorial/public";

    public static final int FILE_NOT_SAVE_ERROR_CODE = 0;

    private Context context;

    public ApiService(Context context){
        this.context = context;
    }

    public void sendFile(File body, String contentType, final MultipartRequestEventListener listener){

        MultipartRequest request = new MultipartRequest(BASE_URL + "/api/v1/fileUpload", body, contentType,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("status");
                            if (success){
                                listener.onMultipartRequestSuccess();
                            } else {
                                listener.onMultipartRequestError(FILE_NOT_SAVE_ERROR_CODE);
                            }
                        } catch (JSONException e) {
                            listener.onMultipartRequestError(e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onMultipartRequestError(error.toString());
                    }
                }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(16000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(context).add(request);
    }
}
