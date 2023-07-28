package com.learn.weighttrackerapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeleteWeightActivity extends AppCompatActivity {

    private WeightDatabase mWeightDB;
    private WeightDao mWeightDao;
    private User mUser;

    private EditText mEditTextDate, mEditTextWeight;
    Button mDeleteButton;
    Date mDate;
    DateFormat mFormatDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_weight);

        mWeightDB = WeightDatabase.getInstance(getApplicationContext());
        mWeightDao = mWeightDB.weightDao();

        mEditTextDate = (EditText) this.findViewById(R.id.editTextDate);
        mEditTextWeight = (EditText) this.findViewById(R.id.editTextWeight);
        mDeleteButton = (Button) this.findViewById(R.id.deleteButton);

        Intent intent = getIntent();
        mUser = (User) intent.getSerializableExtra("user");

        mFormatDate = new SimpleDateFormat("MM/DD/YYYY", Locale.US);
    }

    public void deleteButton(View view) {
        try {
            String dateString = mEditTextDate.getText().toString();
            mDate = mFormatDate.parse(dateString);

            //Check DB for existing date
            Weight existingDate = mWeightDao.getRecordWithDate(mUser.getUsername(), mDate);
            if (existingDate == null){
                Toast.makeText(this, "Date not found. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            //Confirm user wants to delete entry
            AlertDialog.Builder builder = new AlertDialog.Builder(DeleteWeightActivity.this);
            builder.setTitle("Are you sure you want to delete this entry?");
            //If yes
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    List<Weight> userWeights = mWeightDao.getDailyWeightsOfUser(mUser.getUsername());
                    mWeightDao.deleteUserWeight(mUser.getUsername(), mDate);
                }
            });
            //If no
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();


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


