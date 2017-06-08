package com.oauth.georgew.pinchtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Circumference extends AppCompatActivity {

    private static final String TAG = Circumference.class.getSimpleName();
    OkHttpClient client;
    EditText neck_input, chest_input, upper_arm_input, fore_arm_input, waist_input, hips_input, thigh_input, calf_input;
    String neck, chest, upper_arm, fore_arm, waist, hips, thigh, calf;
    Button add_measurements_button;
    String user_id, responseStr;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circumference);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        user_id = getIntent().getStringExtra("user_id");

        neck_input = (EditText) findViewById(R.id.neck_input);
        chest_input = (EditText) findViewById(R.id.chest_input);
        upper_arm_input = (EditText) findViewById(R.id.upperarm_input);
        fore_arm_input = (EditText) findViewById(R.id.forearm_input);
        waist_input = (EditText) findViewById(R.id.waist_input);
        hips_input = (EditText) findViewById(R.id.hips_input);
        thigh_input = (EditText) findViewById(R.id.thigh_input);
        calf_input = (EditText) findViewById(R.id.calf_input);
        add_measurements_button = (Button) findViewById(R.id.calc_circum_button);

        add_measurements_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neck = neck_input.getText().toString();
                chest = chest_input.getText().toString();
                upper_arm = upper_arm_input.getText().toString();
                fore_arm = fore_arm_input.getText().toString();
                waist = waist_input.getText().toString();
                hips = hips_input.getText().toString();
                thigh = thigh_input.getText().toString();
                calf = calf_input.getText().toString();

                String json = "{'neck_circ': '" + neck + "', 'chest_circ': '" + chest + "', 'upper_arm_circ': '" + upper_arm + "', " +
                        "'fore_arm_circ': '" + fore_arm + "', 'waist_circ': '" + waist + "', 'hip_circ': '" + hips + "', 'thigh_circ': '" + thigh + "', 'calf_circ': '" + calf + "'}";
                makePostRequest("https://bodyfatpinchtest.appspot.com/measurements/" + user_id, json);
                finish();
            }
        });

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
                            Log.d(TAG, resp);
                        } catch (JSONException je){
                            je.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void makePostRequest(String url, String json){
        client = new OkHttpClient();
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
                    responseStr = response.body().string();
                    Log.d(TAG, response.toString());
                    Log.d(TAG, responseStr);
                } else {
                    Log.d(TAG, "BLEW IT " + response.toString());
                }
            }
        });
    }

}
