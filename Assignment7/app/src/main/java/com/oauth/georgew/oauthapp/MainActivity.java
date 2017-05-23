package com.oauth.georgew.oauthapp;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String CLIENT_ID = "327991664103-3ij54mg8q56qeclp6a1i4jjn7jtfk8uj.apps.googleusercontent.com";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void createAuthState(){
        Uri authEndPoint = new Uri.Builder().scheme("https").authority("accounts.google.com").path("o/oauth2/v2/auth").build();
        Uri tokenEndPoint = new Uri.Builder().scheme("https").authority("www.googleapis.com").path("/oauth2/v4/token").build();
        Uri redirect = new Uri.Builder().scheme("com.oauth.georgew.oauthapp").path("foo").build();
    }
}
