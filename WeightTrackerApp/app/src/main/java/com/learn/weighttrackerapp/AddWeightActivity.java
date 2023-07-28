package com.learn.weighttrackerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddWeightActivity extends AppCompatActivity {

    DateFormat mFormatDate;
    Date mDate;


    private EditText mEditTextDate, mEditTextWeight;
    Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.getSupportActionBar().hide();
            setContentView(R.layout.activity_add_weight);


            mEditTextDate = (EditText) this.findViewById(R.id.editTextDate);
            mEditTextWeight = (EditText) this.findViewById(R.id.editTextWeight);
            mSaveButton = (Button) this.findViewById(R.id.buttonSave);

            mFormatDate = new SimpleDateFormat("MM/DD/YYYY", Locale.US);

    }

    public void saveButton(View view) {
        try {
            //Get and format date
            String dateString = mEditTextDate.getText().toString();
            mDate = mFormatDate.parse(dateString);

            //Get and format weight
            String weightString = mEditTextWeight.getText().toString();
            double weight = Double.parseDouble(weightString);

            //Create new weight object
            Weight newWeight = new Weight();
            newWeight.setDate(mDate);
            newWeight.setWeight(weight);

            //Return object to weight table for viewing
            Intent returnIntent = getIntent();
            returnIntent.putExtra("newDailyWeight", newWeight);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Return user to main weight tracking activity
    public void cancelButton(){
        Intent returnIntent = getIntent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
