package com.oauth.georgew.pinchtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by georgew on 6/8/17.
 */

public class Common {



    public static void logOut(Context context){
        makeToast("Logout", context);
        clearSharedPreference(context);
    }

    public static void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public static void makeToast(String input, Context context){
        Toast.makeText(context, input, Toast.LENGTH_SHORT).show();
    }

    //https://stackoverflow.com/questions/22186778/using-math-round-to-round-to-one-decimal-place
    public static Double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static String calcLeanBodyMass(Double bodyfat, Double weight_output){
        return round((weight_output - (weight_output * (bodyfat/100))),1).toString();
    }

    public static String calcFatMass(Double bodyfat, Double weight_output){
        return round((weight_output * (bodyfat/100)),1).toString();
    }

    public static Double calcBMI(int height, Double weight){
        return round((weight * 703)/(Math.pow(height,2)),1);
    }

    public static String heightOutput(int height){
        int feet = height/12;
        int inches = height%12;
        return (feet + "\"" + inches + "'");
    }

    public static void deleteUser(String user) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://bodyfatpinchtest.appspot.com/user/" + user;
        Request request = new Request.Builder()
                .url(url)
                .delete()
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
            }
        });
    }

    public static void goToUpdateUserInfo(String user_id, Context context){
        Intent update_user = new Intent(context.getApplicationContext(), UpdateUserInfo.class);
        update_user.putExtra("user_id", user_id);
        context.startActivity(update_user);
    }
}
