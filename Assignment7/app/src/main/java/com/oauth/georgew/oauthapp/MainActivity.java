package com.oauth.georgew.oauthapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String CLIENT_ID = "327991664103-3ij54mg8q56qeclp6a1i4jjn7jtfk8uj.apps.googleusercontent.com";
    private AuthorizationService completeAuthorizationService;
    private AuthState authState;
    TextView access_token;
    private OkHttpClient client;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences authPreference = getSharedPreferences("auth", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        completeAuthorizationService = new AuthorizationService(this);

        Button getPosts = (Button)findViewById(R.id.get_posts);
        getPosts.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                makeGetRequestAndResponse();
             }
        });


        Button post_to_goog = (Button) findViewById(R.id.post_to_google);
        post_to_goog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText input_text = (EditText) findViewById(R.id.goog_input);
                final String user_input = input_text.getText().toString();
                createNewPost(user_input);
            }
        });
    }


    public void createNewPost(final String user_input) {

        final TextView debug_text = (TextView) findViewById(R.id.debug_text);

        try{
            authState.performActionWithFreshTokens(completeAuthorizationService, new AuthState.AuthStateAction(){
                @Override
                public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException authorizationException){
                    if(authorizationException == null) {
                        OkHttpClient client = new OkHttpClient();
                        debug_text.setText(user_input);
                        String json = "{ 'object': { 'originalContent': '" +  user_input + "' }, 'access': { 'items':  [ { 'type': 'domain' } ], 'domainRestricted': true } }";
                        HttpUrl url = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
                        url = url.newBuilder().addQueryParameter("key", "AIzaSyCYCwKDOPKVqhpwLeQP6FS5aqQDU-T2JJI").build();
                        final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(mediaType, json);
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .addHeader("Authorization", "Bearer " + accessToken)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            String resp = response.body().string();
                        } catch(IOException ie){
                            ie.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception pe){
            pe.printStackTrace();
        }
    }


    public void makeGetRequestAndResponse(){
        try {
            authState.performActionWithFreshTokens(completeAuthorizationService, new AuthState.AuthStateAction() {
                @Override
                public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException authorizationException) {
                    if(authorizationException == null){
                        client = new OkHttpClient();
                        HttpUrl url = HttpUrl.parse("https://www.googleapis.com/plusDomains/v1/people/me/activities/user");
                        url = url.newBuilder().addQueryParameter("key", "AIzaSyCYCwKDOPKVqhpwLeQP6FS5aqQDU-T2JJI").build();
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
                                try{
                                    Log.d(TAG, response.toString());
                                    JSONObject jsonObject = new JSONObject(resp);
                                    JSONArray items = jsonObject.getJSONArray("items");
                                    List<Map<String,String>> posts = new ArrayList<Map<String,String>>();
                                    //final String title = items.getJSONObject(0).getString("title");
                                    for(int i = 0; i < items.length(); i++){
                                        HashMap<String, String> m = new HashMap<String, String>();
                                        m.put("published", items.getJSONObject(i).getString("published"));
                                        m.put("title",items.getJSONObject(i).getString("title"));
                                        posts.add(m);
                                    }
                                    final SimpleAdapter postAdapter = new SimpleAdapter(
                                            MainActivity.this,
                                            posts,
                                            R.layout.post_item,
                                            new String[]{"published", "title"},
                                            new int[]{R.id.item_one, R.id.item_two});
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((ListView) findViewById(R.id.post_list)).setAdapter(postAdapter);
                                        }
                                    });

                                } catch (JSONException e1){
                                    Log.d(TAG, response.toString());
                                    Log.d(TAG, "FAILURE RESPONSE");
                                    e1.printStackTrace();
                                }

                            }
                        });
                    }
                }
            });

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart(){
        authState = getOrCreateAuthState();
        super.onStart();
    }

    AuthState getOrCreateAuthState(){
        AuthState authorization = null;
        SharedPreferences authorizationPreference = getSharedPreferences("auth", MODE_PRIVATE);
        String stateJson = authorizationPreference.getString("stateJson", null);
        if(stateJson != null){
            try {
                authorization = AuthState.jsonDeserialize(stateJson);
            } catch (JSONException je){
                je.printStackTrace();
                return null;
            }
        }
        if (authorization != null && authorization.getAccessToken() != null){
            Log.d(TAG, authorization.getAccessToken());
            //access_token.setText(authorization.getAccessToken());
            return authorization;
        } else {
            createAuthState();
            return null;
        }
    }

    void createAuthState(){
        Uri authEndPoint = new Uri.Builder().scheme("https").authority("accounts.google.com").path("o/oauth2/v2/auth").build();
        Uri tokenEndPoint = new Uri.Builder().scheme("https").authority("www.googleapis.com").path("/oauth2/v4/token").build();
        Uri redirect = new Uri.Builder().scheme("com.oauth.georgew.oauthapp").path("path").build();

        AuthorizationServiceConfiguration config = new AuthorizationServiceConfiguration(authEndPoint, tokenEndPoint, null);
        AuthorizationRequest req = new AuthorizationRequest.Builder(config, "327991664103-3ij54mg8q56qeclp6a1i4jjn7jtfk8uj.apps.googleusercontent.com", ResponseTypeValues.CODE, redirect)
                .setScopes("https://www.googleapis.com/auth/plus.me", "https://www.googleapis.com/auth/plus.stream.write", "https://www.googleapis.com/auth/plus.stream.read").build();

        Intent authComplete = new Intent(this, AuthComplete.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, req.hashCode(), authComplete, 0);
        completeAuthorizationService.performAuthorizationRequest(req, pendingIntent);
    }

    void PostToGoog(){

    }
}
