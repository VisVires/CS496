package com.oauth.georgew.pinchtest;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String CLIENT_ID;
    private static final String TAG = MainActivity.class.getSimpleName();
    Button bodyFatButton;
    Button circumButton;
    private AuthorizationService authorizationService;
    private AuthState authState;
    private OkHttpClient client;
    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up test
        test = (TextView)findViewById(R.id.test);
        //get user info using get request to google plus


        //set up authorization
        CLIENT_ID = getString(R.string.CLIENT_ID);
        SharedPreferences authPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        authorizationService = new AuthorizationService(this);

        //set up circumference button
        circumButton = (Button)findViewById(R.id.circum_button);
        circumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getCircum = new Intent(getApplicationContext(), Circumference.class);
                startActivity(getCircum);
            }
        });

        //set up body fat button
        bodyFatButton = (Button)findViewById(R.id.calc_button);
        bodyFatButton.setOnClickListener(new View.OnClickListener(){
          @Override
            public void onClick(View v){
              Intent getBodyFat = new Intent(getApplicationContext(), Pinches.class);
              startActivity(getBodyFat);
          }
        });
        //Log.d(TAG, CLIENT_ID);
    }

    public void makeGetRequestToGoogle(){
        try{
            authState.performActionWithFreshTokens(authorizationService, new AuthState.AuthStateAction(){
                public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException ae) {
                    if(ae == null){
                        client = new OkHttpClient();
                        Log.d(TAG, accessToken);
                        HttpUrl url = HttpUrl.parse("https://www.googleapis.com/plus/v1/people/me");
                        url = url.newBuilder().addQueryParameter("key", getString(R.string.API_KEY)).build();
                        Request request = new Request.Builder()
                                .url(url)
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d(TAG, "FAILURE REQUEST");
                                e.printStackTrace();
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String resp = response.body().string();
                                Log.d(TAG, response.toString());
                                test.setText(response.toString());
                            }
                        });
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart(){
        authState = getOrCreateAuthState();
        super.onStart();
    }

    AuthState getOrCreateAuthState(){
        AuthState auth = null;
        SharedPreferences authorizationPreference = getSharedPreferences("auth", MODE_PRIVATE);
        String stateJson = authorizationPreference.getString("stateJson", null);

        //if stateJson exists set auth to it
        if(stateJson != null){
            try{
                auth = AuthState.jsonDeserialize(stateJson);
            } catch (JSONException je){
                je.printStackTrace();
                return null;
            }
        }
        //if AuthState exists already and has an access token then return else create an AuthState and get a new token
        if(auth != null && auth.getAccessToken() != null){
            //Log.d(TAG, auth.getAccessToken());
            return auth;
        } else {
            createAuthState();
            return null;
        }
    }

    void createAuthState(){
        Uri authEndPoint = new Uri.Builder().scheme("https").authority("accounts.google.com").path("o/oauth2/v2/auth").build();
        Uri tokenEndPoint = new Uri.Builder().scheme("https").authority("www.googleapis.com").path("/oauth2/v4/token").build();
        Uri redirect = new Uri.Builder().scheme("com.oauth.georgew.pinchtest").path("path").build();

        AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(authEndPoint, tokenEndPoint, null);
        AuthorizationRequest req = new AuthorizationRequest.Builder(config, CLIENT_ID, ResponseTypeValues.CODE, redirect)
                .setScopes("email profile openid").build();
                //.setLoginHint("jdoe@user.example.com").build();

        Intent authComplete = new Intent(this, AuthComplete.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, req.hashCode(), authComplete, 0);
        authorizationService.performAuthorizationRequest(req, pendingIntent);
    }
}
