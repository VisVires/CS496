package com.androidui.georgew.myapplication;


import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MainActivity extends Activity {

    Button mSQLButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mSQLButton = (Button) findViewById(R.id.sqlbutton);
        mSQLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sqlLite = new Intent(getApplicationContext(), SQLiteActivity.class);
                startActivity(sqlLite);
            }
        });
    }
}