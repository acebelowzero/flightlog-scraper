package com.example.mainacitvity;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyService extends Service {
    Uri resultData;
    String resul;
    private Date c = Calendar.getInstance().getTime();
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;
    StringBuilder text = new StringBuilder();
    StringBuilder contentText = new StringBuilder();
    private final OkHttpClient client = new OkHttpClient();
    public MyService() {
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        resul = intent.getStringExtra("dir");
        resultData = Uri.parse(resul);
        startHandler();
        return START_STICKY;
    }

    private void startHandler() {
        handler.postDelayed(runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                handler.postDelayed(runnable, delay);
                Log.e("ServiceAct", "test");
                beginDataRead();

            }
        }, delay);
    }


    private void beginDataRead(){
        Uri uri = null;
        String FileName = "log";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = df.format(c);


        if(resultData != null) {
            uri = resultData;
            DocumentFile documentFile = DocumentFile.fromTreeUri(this, uri);
            FileName = "log-"+formatDate+".txt";
            DocumentFile file = documentFile.findFile(FileName);

            if(file != null) {
                try {
                    readTextFromUri(file.getUri());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                df.format(cal);
                file = documentFile.findFile(FileName);
                try {
                    readTextFromUri(file.getUri());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<String> content = new ArrayList<String>();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String temp = "";
            while((temp = reader.readLine()) != null) {
                content.add(temp);
            }
            int size = content.size();
            stringBuilder.append(content.get(size-1));
//            showText.setText(stringBuilder);

            makePost(stringBuilder.toString());
        }
    }


    private void makePost(String droneData) throws IOException {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                droneData
        );
        Request request = new Request.Builder()
                .url("http://18.253.63.137:2525/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                showText.setText(e.toString());
                Log.e("MyActivity", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.w("MyActivity", response.body().string());
                Log.i("MyActivity", response.toString());

            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}