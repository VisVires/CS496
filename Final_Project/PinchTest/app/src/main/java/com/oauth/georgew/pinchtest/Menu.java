package com.oauth.georgew.pinchtest;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Menu extends AppCompatActivity {

    private static final String TAG = Menu.class.getSimpleName();
    Button bodyFatButton;
    Button circumButton;
    private OkHttpClient client;
    TextView greeting;
    String gender, user_id, first_name, last_name, email;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        greeting = (TextView) findViewById(R.id.greeting);
        gender = getIntent().getStringExtra("gender");
        user_id = getIntent().getStringExtra("user_id");
        first_name = getIntent().getStringExtra("first_name");
        last_name = getIntent().getStringExtra("last_name");
        email = getIntent().getStringExtra("email");
        greeting.setText("Hello " + first_name + " " + last_name + "!");

                //set up circumference button
        circumButton = (Button) findViewById(R.id.circum_button);
        circumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getCircum = new Intent(getApplicationContext(), Circumference.class);
                postUserInfoToApi();
                //startActivity(getCircum);
            }
        });

        //set up body fat button
        bodyFatButton = (Button) findViewById(R.id.calc_button);
        bodyFatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getBodyFat = new Intent(getApplicationContext(), Pinches.class);
                postUserInfoToApi();
            }
        });
        //Log.d(TAG, CLIENT_ID);
    }

    public void postUserInfoToApi(){
        client = new OkHttpClient();
        final String url = "https://bodyfatpinchtest.appspot.com";
        final String json = "{'first_name': '" + first_name + "', 'last_name': '" + last_name + "', 'email': '" + email + "', 'user': '" + user_id + "', 'gender': '" + gender + "'}";
        //build url
        Log.d(TAG, json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "FAILURE REQUEST");
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //set up test
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d(TAG, response.toString());
                    Log.d(TAG, responseStr);
                } else {
                    Log.d(TAG, "BLEW IT" + response.toString());
                }
            }
        });
    }
}