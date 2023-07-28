package com.learn.weighttrackerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SetGoalActivity extends AppCompatActivity {

    private WeightDatabase mWeightDB;
    private GoalWeightDao mGoalWeightDao;
    private User mUser;
    EditText mSetGoalWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        //Get single instance of database
        mWeightDB = WeightDatabase.getInstance(getApplicationContext());
        mGoalWeightDao = mWeightDB.goalWeightDao();

        //Set goal weight to current user info
        Intent intent = getIntent();
        mUser = (User) intent.getSerializableExtra("user");

        mSetGoalWeight = findViewById(R.id.editTextGoalWeight);

    }

    public void saveGoalWeight(){
        String goalWeightString = mSetGoalWeight.getText().toString();
        double goalWeight = Double.parseDouble(goalWeightString);
        //Change goal weight in DB
        mGoalWeightDao.setGoalWeight(goalWeight, mUser.getUsername());

        Intent returnIntent = getIntent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    //Return user to main weight tracking activity
    public void cancelButton(){
        Intent returnIntent = getIntent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}
