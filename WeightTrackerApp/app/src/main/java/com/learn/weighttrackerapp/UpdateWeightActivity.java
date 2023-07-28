package com.learn.weighttrackerapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpdateWeightActivity extends AppCompatActivity {

    private WeightDatabase mWeightDB;
    private WeightDao mWeightDao;
    private User mUser;

    private EditText mEditTextDate, mEditTextWeight;
    private Button mUpdateButton;

    Date mDate;
    DateFormat mFormatDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_weight);

        mWeightDB = WeightDatabase.getInstance(getApplicationContext());
        mWeightDao = mWeightDB.weightDao();

        // get views from layout file
        mEditTextDate = (EditText) this.findViewById(R.id.editTextDate);
        mEditTextWeight = (EditText) this.findViewById(R.id.editTextWeight);
        mUpdateButton = (Button) this.findViewById(R.id.updateButton);

        Intent intent = getIntent();
        mUser = (User) intent.getSerializableExtra("user");

        mFormatDate = new SimpleDateFormat("MM/DD/YYYY", Locale.US);
    }

    public void updateButton(View view) {
        try {
            //Format date
            String dateString = mEditTextDate.getText().toString();
            mDate = mFormatDate.parse(dateString);

            //Check DB for existing date
            Weight existingDate = mWeightDao.getRecordWithDate(mUser.getUsername(), mDate);
            if (existingDate == null){
                Toast.makeText(this, "Date not found. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            //Format weight
            String weightString = mEditTextWeight.getText().toString();
            double newWeight = Double.parseDouble(weightString);

            //Update entry in DB
            mWeightDao.updateDailyWeight(mUser.getUsername(),mDate, newWeight);

            //Return value to weight activity main
            Intent returnIntent = getIntent();
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
