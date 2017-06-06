package com.oauth.georgew.pinchtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Pinches extends AppCompatActivity {

    private static final String TAG = Pinches.class.getSimpleName();
    EditText weight, bicep, tricep, subscap, suprailiac;
    Button updateBodyFat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinches);

        //set up button and fields
        updateBodyFat = (Button)findViewById(R.id.body_fat_button);
        weight = (EditText)findViewById(R.id.weight_input);
        bicep = (EditText)findViewById(R.id.bicep_pinch_input);
        tricep = (EditText)findViewById(R.id.tricep_pinch_input);
        subscap = (EditText)findViewById(R.id.subscap_pinch_input);
        suprailiac = (EditText)findViewById(R.id.suprailiac_pinch_input);


        //set button to only function if all 4 pinch fields are filled out
        updateBodyFat.setEnabled(false);
        bicep.addTextChangedListener(textWatcher);
        tricep.addTextChangedListener(textWatcher);
        subscap.addTextChangedListener(textWatcher);
        suprailiac.addTextChangedListener(textWatcher);

        updateBodyFat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView bodyFatText = (TextView)findViewById(R.id.body_fat_text);
                TextView bodyFatOutput = (TextView)findViewById(R.id.body_fat_output);
                TextView fatMassText = (TextView)findViewById(R.id.fat_mass_text);
                TextView fatMass = (TextView)findViewById(R.id.fat_mass_output);
                TextView leanBodyMassText = (TextView)findViewById(R.id.lean_body_mass_text);
                TextView leanBodyMass = (TextView)findViewById(R.id.lean_body_mass_output);

                bodyFatText.setText(getResources().getString(R.string.bodyfat));
                fatMassText.setText(getResources().getString(R.string.fatmass));
                leanBodyMassText.setText(getResources().getString(R.string.leanbodymass));
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if(bicep.getText().toString().length() == 0 || tricep.getText().toString().length() == 0 || subscap.getText().toString().length() == 0 || suprailiac.getText().toString().length() == 0)
            {
                updateBodyFat.setEnabled(false);
            } else {
                updateBodyFat.setEnabled(true);
            }
        }
    };
}
