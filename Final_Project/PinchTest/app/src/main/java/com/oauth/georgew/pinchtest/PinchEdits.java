package com.oauth.georgew.pinchtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.oauth.georgew.pinchtest.UpdateUserInfo.user_id;

public class PinchEdits extends AppCompatActivity {

    Button edit_button, back_to_update_button;
    String pinch_id;
    OkHttpClient client;
    private static final String TAG = PinchEdits.class.getSimpleName();

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_user: {
                Common.goToUpdateUserInfo(user_id, this);
                return true;
            }
            case R.id.action_sign_out: {
                Common.logOut(this);
                this.finishAffinity();
                return true;
            }
            case R.id.delete_profile: {
                Common.deleteUser(user_id);
                Common.makeToast("Deleted User", this);
                Intent home = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(home);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinch_edits);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        back_to_update_button = (Button) findViewById(R.id.pinch_edit_back_button);
        edit_button = (Button) findViewById(R.id.update_body_fat_button);


        back_to_update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void getPinches(){
        client = new OkHttpClient();
        String url = "https://bodyfatpinchtest.appspot.com/pinchtest/" + pinch_id;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "FAILURE REQUEST 1");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resp = response.body().string();
                Log.d(TAG, resp);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }
}
