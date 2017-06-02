package com.oauth.georgew.pinchtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    Button bodyFatButton;
    Button circumButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}
