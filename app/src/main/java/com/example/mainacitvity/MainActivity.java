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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 2000;
    StringBuilder text = new StringBuilder();
    StringBuilder contentText = new StringBuilder();


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
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if(requestCode==REQUEST_EXTERNAL_STORAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            String FileName = "log";

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formatDate = df.format(c);


            if(resultData != null) {
                uri = resultData.getData();
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
        super.onActivityResult(requestCode, resultCode, resultData);
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
            showText.setText(stringBuilder);
        }
    }


//    @Override
//    protected void onResume() {
//        handler.postDelayed(runnable = new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            public void run() {
//                handler.postDelayed(runnable, delay);
//                showText.setText("test");
//                readandSendData();
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


//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void readandSendData() {
//        try{
//            String cont = "";
//            file = "log";
//            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            LocalDateTime now = LocalDateTime.now();
//            String dt = dtf.format(now);
//            file = file+"-"+dt+".txt";
//            File ffile = new File(dir, file);
//            BufferedReader fIn = new BufferedReader(new FileReader(ffile));
//            String temp = "";
//            while((temp = fIn.readLine()) != null) {
//                content.add(temp);
//            }
//            int size = content.size();
//            cont = content.get(size-1);
////            postDataUsingVolley(cont, cont);
//            fIn.close();
//            showText.setText(cont);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void postDataUsingVolley(String name, String job) {
//        // url to post our data
//        String url = "http://18.253.63.137:2525/";
//
//
//        // creating a new variable for our request queue
//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//
//        // on below line we are calling a string
//        // request method to post the data to our API
//        // in this we are calling a post method.
//        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                // inside on response method we are
//                // hiding our progress bar
//                // and setting data to edit text as empty
//
//                // on below line we are displaying a success toast message.
////                Toast.makeText(MainActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
//                System.out.println("Data added");
//                try {
//                    // on below line we are passing our response
//                    // to json object to extract data from it.
//                    JSONObject respObj = new JSONObject(response);
//
//                    // below are the strings which we
//                    // extract from our json object.
//                    String name = respObj.getString("name");
//                    String job = respObj.getString("job");
//
//                    // on below line we are setting this string s to our text view.
////                    responseTV.setText("Name : " + name + "\n" + "Job : " + job);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // method to handle errors.
////                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
//                System.out.println("failed to get response");
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//
//                // on below line we are passing our key
//                // and value pair to our parameters.
//                params.put("name", name);
//                params.put("job", job);
//
//                // at last we are
//                // returning our params.
//                return params;
//            }
//        };
//        // below line is to make
//        // a json object request.
//        queue.add(request);
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