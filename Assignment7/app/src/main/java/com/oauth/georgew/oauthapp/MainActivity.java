package com.oauth.georgew.oauthapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    String CLIENT_ID = "327991664103-3ij54mg8q56qeclp6a1i4jjn7jtfk8uj.apps.googleusercontent.com";
    private AuthorizationService completeAuthorizationService;
    private AuthState authState;
    TextView access_token;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences authPreference = getSharedPreferences("auth", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        completeAuthorizationService = new AuthorizationService(this);
        /*authState.performActionWithFreshTokens(completeAuthorizationService, new AuthState.AuthStateAction() {
            @Override
            public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException e) {
                access_token.setText(accessToken);
            }
        });*/
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
            access_token = (TextView) findViewById(R.id.access_token);
            access_token.setText(authorization.getAccessToken());
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
        if (req != null && pendingIntent != null){
            Log.d(TAG, "This should work right?");
            completeAuthorizationService.performAuthorizationRequest(req, pendingIntent);
        } else {
            Log.d(TAG, "Doesn't work");
        }
    }
}