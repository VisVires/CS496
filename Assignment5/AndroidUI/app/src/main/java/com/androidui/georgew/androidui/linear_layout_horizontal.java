package com.androidui.georgew.androidui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class linear_layout_horizontal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout_horizontal);

        TextView horizView;
        Button btnAdd = (Button) findViewById(R.id.btnAddNumber);
        Button btnClose = (Button) findViewById(R.id.btnClose);
        final Integer [] numbers = new Integer[]{1};

        final List<Integer> numberList = new ArrayList<Integer>(Arrays.asList(numbers));

        btnClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Closing linear_layout_horizontal Activity
                finish();
            }
        });

        horizView = (TextView) findViewById(R.id.addTextHoriz);

        final ArrayAdapter<Integer> horizViewAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, numberList);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer highestNum = numberList.get(numberList.size() - 1) + 1;
                numberList.add(numberList.size(), highestNum);
                horizViewAdapter.notifyDataSetChanged();


                // Confirm the addition
                Toast.makeText(getApplicationContext(),
                        "Item added : " + highestNum, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
