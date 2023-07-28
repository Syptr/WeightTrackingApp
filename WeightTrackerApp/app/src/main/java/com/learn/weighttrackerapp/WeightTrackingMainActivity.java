package com.learn.weighttrackerapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WeightTrackingMainActivity extends AppCompatActivity {
    private WeightDatabase mWeightDB;
    private WeightDao mWeightDao;
    private GoalWeightDao mGoalWeightDao;
    private User mUser;

    private int SMS_USER_PERMISSION_CODE = 1;

    private final int ADD_WEIGHT_ACTIVITY = 1;
    private final int SET_GOAL_ACTIVITY = 2;
    private final int UPDATE_WEIGHT_ACTIVITY = 3;
    private final int DELETE_WEIGHT_ACTIVITY = 4;

    Weight mNewWeight;
    TableLayout mTableLayout;
    TextView mGoalWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_weight_tracking_main);

        mTableLayout = (TableLayout) findViewById(R.id.dailyWeightTable);
        mGoalWeight = (TextView) findViewById(R.id.goalWeightText);

        //Get single instance of DB
        mWeightDB = WeightDatabase.getInstance(getApplicationContext());
        mWeightDao = mWeightDB.weightDao();

        //Get the current user passed from login
        mUser = (User) getIntent().getSerializableExtra("user");

        updateTable();
        updateTargetWeight();
        requestSmsPermissions();
    }


    public void updateTable() {

        //Get all weight entries from current user
        List<Weight> userDailyWeights = mWeightDao.getDailyWeightsOfUser(mUser.getUsername());


        DateFormat formatter = new SimpleDateFormat("MM/DD/YYYY", Locale.US);
        TableRow header = (TableRow) findViewById(R.id.headerRow);
        TextView headerDate = (TextView) findViewById(R.id.headerDate);
        TextView headerWeight = (TextView) findViewById(R.id.headerWeight);


        TableLayout.LayoutParams layoutParamsTable = (TableLayout.LayoutParams) header.getLayoutParams();
        TableRow.LayoutParams layoutParamsRow = (TableRow.LayoutParams) headerDate.getLayoutParams();

        for (int i = 0; i < userDailyWeights.size(); i++) {
            TableRow row = new TableRow(this);
            TextView dateTextView = new TextView(this);
            TextView weightTextView = new TextView(this);

            // activate the layout parameters
            row.setLayoutParams(layoutParamsTable);
            dateTextView.setLayoutParams(layoutParamsRow);
            weightTextView.setLayoutParams(layoutParamsRow);
            // set additional view properties
            dateTextView.setWidth(0);
            dateTextView.setGravity(Gravity.CENTER);
            dateTextView.setPadding(20, 20, 20, 20);
            weightTextView.setWidth(0);
            weightTextView.setGravity(Gravity.CENTER);
            weightTextView.setPadding(20, 20, 20, 20);


            dateTextView.setText(formatter.format(userDailyWeights.get(i).getDate()));
            weightTextView.setText(Double.toString(userDailyWeights.get(i).getWeight()));


            row.addView(dateTextView);
            row.addView(weightTextView);


            mTableLayout.addView(row);
        }
    }

    //Update goal weight when user changes value
    public void updateTargetWeight() {
        mGoalWeightDao = mWeightDB.goalWeightDao();
        GoalWeight currentGoal = mGoalWeightDao.getSingleGoalWeight(mUser.getUsername());
        mGoalWeight.setText(currentGoal.getGoalWeight() + "");
    }


    //Add new weight entry
    public void addWeight(View view) {
        try {
            Intent intent = new Intent(this, AddWeightActivity.class);
            startActivityForResult(intent, ADD_WEIGHT_ACTIVITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Set or update goal weight
    public void setGoalWeight(View view) {
        try {
            Intent intent = new Intent(this, SetGoalActivity.class);
            intent.putExtra("user", mUser);
            startActivityForResult(intent, SET_GOAL_ACTIVITY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Update weight entry
    public void updateEntry(View view) {
        try {
            Intent intent = new Intent(this, UpdateWeightActivity.class);
            intent.putExtra("user", mUser);
            startActivityForResult(intent, UPDATE_WEIGHT_ACTIVITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Delete weight entry
    public void deleteEntry(View view) {
        try {
            Intent intent = new Intent(this, DeleteWeightActivity.class);
            intent.putExtra("user", mUser);
            startActivityForResult(intent, DELETE_WEIGHT_ACTIVITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == ADD_WEIGHT_ACTIVITY) {
                    mNewWeight = (Weight) data.getSerializableExtra("newDailyWeight");
                    mNewWeight.setUsername(mUser.getUsername());

                    mWeightDao.insertWeight(mNewWeight);
                    updateTable();
                    updateTargetWeight();
                    goalReached();
                    Toast.makeText(WeightTrackingMainActivity.this, "New entry added",
                            Toast.LENGTH_LONG).show();
            }

            //Set goal weight
            if (requestCode == SET_GOAL_ACTIVITY) {
                updateTable();
                updateTargetWeight();
                goalReached();
                Toast.makeText(WeightTrackingMainActivity.this, "Goal weight successfully updated",
                        Toast.LENGTH_LONG).show();

            }

            //Update a weight entry
            if (requestCode == UPDATE_WEIGHT_ACTIVITY) {
                updateTable();
                updateTargetWeight();
                goalReached();
                Toast.makeText(WeightTrackingMainActivity.this, "Entry updated",
                        Toast.LENGTH_LONG).show();

            }

            //Delete weight entry
            if (requestCode == DELETE_WEIGHT_ACTIVITY) {
                updateTable();
                updateTargetWeight();
                Toast.makeText(WeightTrackingMainActivity.this, "Entry deleted",
                        Toast.LENGTH_LONG).show();
            }


        }
    }


    //Request permission from user to send an SMS message when they reach their goal weight
    private void requestSmsPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to alert you with an SMS message " +
                            "when you've reached your goal weight.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(WeightTrackingMainActivity.this, new String[]
                                    {Manifest.permission.SEND_SMS}, SMS_USER_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.SEND_SMS}, SMS_USER_PERMISSION_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SMS_USER_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS PERMISSION ALREADY GRANTED", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
        }
    }

    //Send notification when user reaches goal weight
    public void sendSMSMessage(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("DESTINATION ADDRESS GOES HERE", null, "Congrats! You've reached your goal weight." +
                    "Keep up the great work!", null, null);
        }
    }

    //Check for when goal weight is reached so notification can be sent to user
    private void goalReached(){
        List<Weight> usersWeights = mWeightDao.getDailyWeightsOfUser(mUser.getUsername());

        if (usersWeights.size() != 0){
            double currentWeight = usersWeights.get(usersWeights.size() - 1).getWeight();
            double currentGoalWeight = mGoalWeightDao.getSingleGoalWeight(mUser.getUsername()).getGoalWeight();

            if(currentWeight <= currentGoalWeight){
                sendSMSMessage();
            }
        }
    }


}


