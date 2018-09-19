package com.abbas.ali.serverfiletranfer.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.abbas.ali.serverfiletranfer.R;
import com.abbas.ali.serverfiletranfer.api.ApiService;
import com.abbas.ali.serverfiletranfer.api.MultipartRequestEventListener;

import java.io.File;

public class MainActivity extends AppCompatActivity implements MultipartRequestEventListener {

    private static final String TAG = "MainActivity";

    private static final int PICK_IMAGE_REQUEST_CODE = 1001;
    private static final int PICK_VIDEO_REQUEST_CODE = 1002;

    private static final int PERMISSION_REQUEST_CODE = 1003;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestForPermission();
        } else {
            init();
            setupViews();
        }
    }

    private void requestForPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            init();
            setupViews();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            init();
            setupViews();
        } else if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, getResources().getString(R.string.main_permissionMessage), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void init() {
        apiService = new ApiService(this);
    }

    private void setupViews() {
        Button sendImageButton = findViewById(R.id.btn_main_sendImage);
        Button sendVideoButton = findViewById(R.id.btn_main_sendVideo);

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

        sendVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = getAbsolutePathFromUri(data.getData());
            File file = new File(path);
            String mimeType = getMimeTypeFromUri(data.getData());
            apiService.sendFile(file, mimeType, this);

        } else if (requestCode == PICK_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = getAbsolutePathFromUri(data.getData());
            File file = new File(path);
            String mimeType = getMimeTypeFromUri(data.getData());
            apiService.sendFile(file, mimeType, this);
        }
    }

    private String getAbsolutePathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String path = cursor.getString(0);
            cursor.close();
            return path;
        }
        return "";
    }

    private String getMimeTypeFromUri(Uri uri) {
        return getContentResolver().getType(uri);
    }

    @Override
    public void onMultipartRequestSuccess() {
        Toast.makeText(this, getResources().getString(R.string.main_sendFileSuccess), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMultipartRequestError(String e) {
        Log.d(TAG, "onMultipartRequestError: e = " + e);
    }

    @Override
    public void onMultipartRequestError(int errorCode) {
        switch (errorCode) {
            case ApiService.FILE_NOT_SAVE_ERROR_CODE:
                Toast.makeText(this, "Some Error was Happen . Please try again", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
