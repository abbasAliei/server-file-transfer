package com.abbas.ali.multipartrequest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MultipartRequest extends Request<JSONObject> {

    /**
     * the charset is used in protocol
     */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * the content-type which is set in request headers
     */
    private String protocolContentType;

    /**
     * called when response is received from server to notify user
     */
    private Response.Listener<JSONObject> listener;

    /**
     * the File which should convert to byte array to send to server as request body
     */
    private File body;

    /**
     *
     * @param url the target url
     * @param body to initialize {@link #body}
     * @param contentType to initialize {@link #protocolContentType}
     * @param listener to initialize {@link #listener}
     * @param errorListener the listener for notify user when sending file got any error
     * the constructor to initialize fields
     */
    public MultipartRequest(String url, File body,String contentType, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.listener = listener;
        this.body = body;
        this.protocolContentType = contentType;
    }

    /**
     *
     * @return protocol content-type to set as request header
     */
    @Override
    public String getBodyContentType() {
        return protocolContentType;
    }

    /**
     *
     * @return an array of bytes which should send across Http to server
     */
    @Override
    public byte[] getBody(){
        byte[] bytes = new byte[(int) body.length()];
        try {
            FileInputStream fis = new FileInputStream(body);
            fis.read(bytes);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     *
     * @param response the response which come's from server
     * @return an Response<JSONObject> which is converted from byte array to JSONObject
     */
    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));

        } catch (Exception e){
            return Response.error(new ParseError(e));
        }
    }

    /**
     *
     * @param response a JSONObject which should deliver to user
     */
    @Override
    protected void deliverResponse(JSONObject response) {
        if (listener != null){
            listener.onResponse(response);
        }
    }
}
