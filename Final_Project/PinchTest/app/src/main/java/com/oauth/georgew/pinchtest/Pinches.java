package com.oauth.georgew.pinchtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Pinches extends AppCompatActivity {

    private static final String TAG = Pinches.class.getSimpleName();
    EditText weight_input, bicep_input, tricep_input, subscap_input, suprailiac_input;
    String user_id, responseStr, bicep, tricep, weight, subscap, suprailiac;
    Button updateBodyFat;
    OkHttpClient client;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinches);

        user_id = getIntent().getStringExtra("user_id");

        //set up button and fields
        updateBodyFat = (Button)findViewById(R.id.body_fat_button);
        weight_input = (EditText)findViewById(R.id.weight_input);
        bicep_input = (EditText)findViewById(R.id.bicep_pinch_input);
        tricep_input = (EditText)findViewById(R.id.tricep_pinch_input);
        subscap_input = (EditText)findViewById(R.id.subscap_pinch_input);
        suprailiac_input = (EditText)findViewById(R.id.suprailiac_pinch_input);


        //set button to only function if all 4 pinch fields are filled out
        updateBodyFat.setEnabled(false);
        bicep_input.addTextChangedListener(textWatcher);
        tricep_input.addTextChangedListener(textWatcher);
        subscap_input.addTextChangedListener(textWatcher);
        suprailiac_input.addTextChangedListener(textWatcher);

        updateBodyFat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView bodyFatText = (TextView)findViewById(R.id.body_fat_text);
                TextView bodyFatOutput = (TextView)findViewById(R.id.body_fat_output);
                TextView fatMassText = (TextView)findViewById(R.id.fat_mass_text);
                TextView fatMass = (TextView)findViewById(R.id.fat_mass_output);
                TextView leanBodyMassText = (TextView)findViewById(R.id.lean_body_mass_text);
                TextView leanBodyMass = (TextView)findViewById(R.id.lean_body_mass_output);

                weight = weight_input.getText().toString();
                bicep = bicep_input.getText().toString();
                tricep = tricep_input.getText().toString();
                subscap = subscap_input.getText().toString();
                suprailiac = suprailiac_input.getText().toString();

                bodyFatText.setText(getResources().getString(R.string.bodyfat));
                fatMassText.setText(getResources().getString(R.string.fatmass));
                leanBodyMassText.setText(getResources().getString(R.string.leanbodymass));
                String json = "{'bicep': '" + bicep + "', 'tricep': '" + tricep + "', 'subscapular': '" + subscap + "', 'suprailiac': '" + suprailiac + "'}";
                makePostRequest("https://bodyfatpinchtest.appspot.com/pinchtest/" + user_id, json);
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(bicep_input.getText().toString().length() == 0 || tricep_input.getText().toString().length() == 0 || subscap_input.getText().toString().length() == 0 || suprailiac_input.getText().toString().length() == 0)
            {
                updateBodyFat.setEnabled(false);
            } else {
                updateBodyFat.setEnabled(true);
            }
        }
    };

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
