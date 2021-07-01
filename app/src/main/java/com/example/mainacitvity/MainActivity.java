package com.example.mainacitvity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;


import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int PICKFILE_RESULT_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Date c = Calendar.getInstance().getTime();
    private Button readText;
    private TextView showText;
    private String file = "log";
    String pathToDir = "/mnt/internal_sd/DJI/com.dji.aeroscope/FlightLog/";
    private String dir = "";
    Intent resultData;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;
    StringBuilder text = new StringBuilder();
    StringBuilder contentText = new StringBuilder();
    private final OkHttpClient client = new OkHttpClient();
//    client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
//    client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readText = findViewById(R.id.readText);
        showText = findViewById(R.id.showText);

        verifyStoragePermissions(MainActivity.this);

        readText.setOnClickListener(new View.OnClickListener() {
            StringBuilder text = new StringBuilder();
            ArrayList<String> content = new ArrayList<String>();
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent rdata) {
        if(requestCode==REQUEST_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            resultData = rdata;
            startBackground();
        }
        super.onActivityResult(requestCode, resultCode, rdata);
    }

    public void startBackground() {
        Intent serviceIntent = new Intent(getApplicationContext(), MyService.class);
        serviceIntent.putExtra("dir", resultData.getDataString());
        startService(serviceIntent);
    }

//    private void beginDataRead(){
//        Uri uri = null;
//        String FileName = "log";
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        String formatDate = df.format(c);
//
//
//        if(resultData != null) {
//            uri = resultData.getData();
//            DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);
//            FileName = "log-"+formatDate+".txt";
//            DocumentFile file = documentFile.findFile(FileName);
//
//            if(file != null) {
//                try {
//                    readTextFromUri(file.getUri());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Calendar cal = Calendar.getInstance();
//                cal.add(Calendar.DATE, -1);
//                df.format(cal);
//                file = documentFile.findFile(FileName);
//                try {
//                    readTextFromUri(file.getUri());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//    private void readTextFromUri(Uri uri) throws IOException {
//        StringBuilder stringBuilder = new StringBuilder();
//        ArrayList<String> content = new ArrayList<String>();
//        try (InputStream inputStream =
//                     getContentResolver().openInputStream(uri);
//             BufferedReader reader = new BufferedReader(
//                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
//            String temp = "";
//            while((temp = reader.readLine()) != null) {
//                content.add(temp);
//            }
//            int size = content.size();
//            stringBuilder.append(content.get(size-1));
//            showText.setText(stringBuilder);
//
//            makePost(stringBuilder.toString());
//        }
//    }
//
//
//    private void makePost(String droneData) throws IOException {
//        RequestBody body = RequestBody.create(
//                MediaType.parse("application/json; charset=utf-8"),
//                droneData
//                );
//        Request request = new Request.Builder()
//                .url("http://18.253.63.137:2525/")
//                .post(body)
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                showText.setText(e.toString());
//                Log.e("MyActivity", e.toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.w("MyActivity", response.body().string());
//                Log.i("MyActivity", response.toString());
//
//            }
//        });
//
//
////            Response response = client.newCall(request).execute();
//
////        try (Response response = client.newCall(request).execute()) {
////
////            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
////
////            // Get response body
////            System.out.println(response.body().string());
////        }
//    }
//
//
//    @Override
//    protected void onResume() {
//        handler.postDelayed(runnable = new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                beginDataRead();
//
//            }
//        }, delay);
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
//    }


    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

    }


}