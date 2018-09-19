package com.abbas.ali.serverfiletranfer.api;

public interface MultipartRequestEventListener {
    void onMultipartRequestSuccess();
    void onMultipartRequestError(String e);
    void onMultipartRequestError(int errorCode);
}
