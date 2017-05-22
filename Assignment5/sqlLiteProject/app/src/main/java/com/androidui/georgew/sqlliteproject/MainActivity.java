package com.androidui.georgew.sqlliteproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button sqlButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlButton = (Button) findViewById(R.id.sqlbutton);
        sqlButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent sqlActivity = new Intent(getApplicationContext(), sqlite.class);
                startActivity(sqlActivity);
            }

        });
    }
}
