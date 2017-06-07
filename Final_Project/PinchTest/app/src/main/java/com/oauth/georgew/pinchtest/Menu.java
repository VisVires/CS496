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

import org.json.JSONArray;
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
    TextView greeting, test, age_output, height_output, weight_output;
    String gender, user_id, first_name, last_name, age, weight, height;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        greeting = (TextView) findViewById(R.id.greeting);
        user_id = getIntent().getStringExtra("user_id");

        //makeGetRequest("https://bodyfatpinchtest.appspot.com/user/" + user_id);
        //set up circumference button
        circumButton = (Button) findViewById(R.id.circum_button);
        circumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getCircum = new Intent(getApplicationContext(), Circumference.class);
                getCircum.putExtra("user_id", user_id);
                startActivity(getCircum);
            }
        });

        //set up body fat button
        bodyFatButton = (Button) findViewById(R.id.calc_button);
        bodyFatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getBodyFat = new Intent(getApplicationContext(), Pinches.class);
                getBodyFat.putExtra("user_id", user_id);
                startActivity(getBodyFat);
            }
        });
        //Log.d(TAG, CLIENT_ID);
    }

    @Override
    protected void onStart(){
        makeGetRequest("https://bodyfatpinchtest.appspot.com/user/" + user_id);
        super.onStart();
    }

    public void makeGetRequest(String url) {

        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "FAILURE REQUEST");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resp = response.body().string();
                    //set up test

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject jsonObject = new JSONObject(resp);
                                first_name = jsonObject.getString("first_name");
                                last_name = jsonObject.getString("last_name");
                                age = jsonObject.getString("age");
                                height = jsonObject.getString("height");
                                test = (TextView) findViewById(R.id.test);
                                age_output = (TextView) findViewById(R.id.age);
                                height_output = (TextView) findViewById(R.id.height);
                                weight_output = (TextView) findViewById(R.id.weight);

                                greeting.setText("Welcome Back " + first_name + " " + last_name + "!");
                                age_output.setText(age);
                                Integer height_in_inches = Integer.parseInt(height);
                                Integer feet = height_in_inches/12;
                                Integer inches = height_in_inches%12;
                                height_output.setText(feet + "\"" + inches + "'");
                                Log.d(TAG, resp);
                            } catch (JSONException je){
                                je.printStackTrace();
                            }
                        }
                    });
            }
        });
    }
}
