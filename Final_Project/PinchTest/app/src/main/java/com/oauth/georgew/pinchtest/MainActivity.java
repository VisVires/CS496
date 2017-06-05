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
import android.widget.ListView;
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
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String CLIENT_ID;
    private static final String TAG = MainActivity.class.getSimpleName();
    private AuthorizationService authorizationService;
    private AuthState authState;
    private OkHttpClient client;
    Button get_started;
    TextView test1;
    String gender, user_id, first_name, last_name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up authorization
        CLIENT_ID = getString(R.string.CLIENT_ID);
        SharedPreferences authPreferences = getSharedPreferences("auth", MODE_PRIVATE);
        authorizationService = new AuthorizationService(this);

        get_started = (Button) findViewById(R.id.get_started);
        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeGetRequestToGoogle();
            }
        });
    }


    public void postUserInfoToApi(){
        try{
            authState.performActionWithFreshTokens(authorizationService, new AuthState.AuthStateAction(){
                @Override
                public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException authorizationException){
                    if(authorizationException == null) {
                        client = new OkHttpClient();
                        //debug_text.setText(user_input);
                        //set json string with user input
                        String json = "{ 'first_name': '" +  first_name + "', 'last_name': '" +  last_name + "', 'email': '" +  email + "', 'user': '" +  user_id + "', 'gender': '" +  gender + "' }";
                        //build url
                        Log.d(TAG, json);
                        HttpUrl url = HttpUrl.parse("https://bodyfatpinchtest.appspot.com");
                        final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                        //make request with body
                        RequestBody body = RequestBody.create(mediaType, json);
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();
                        //complete async request
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
                            }
                        });
                    }
                }
            });
        } catch (Exception pe){
            pe.printStackTrace();
        }
    }

    public void makeGetRequestToGoogle(){
        try{
            authState.performActionWithFreshTokens(authorizationService, new AuthState.AuthStateAction(){
                @Override
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
                                final String resp = response.body().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(resp);
                                    gender = jsonObject.getString("gender");
                                    user_id = jsonObject.getString("id");
                                    JSONArray emails = jsonObject.getJSONArray("emails");
                                    email = emails.getJSONObject(0).getString("value");
                                    JSONObject name = jsonObject.getJSONObject("name");
                                    last_name = name.getString("familyName");
                                    first_name = name.getString("givenName");
                                    //set up test
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            test1 = (TextView) findViewById(R.id.test1);
                                            test1.setText(email);
                                            postUserInfoToApi();
                                            Intent menu = new Intent(getApplicationContext(), Menu.class);
                                            //startActivity(menu);
                                        }
                                    });
                                } catch (JSONException je){
                                    je.printStackTrace();
                                }

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
