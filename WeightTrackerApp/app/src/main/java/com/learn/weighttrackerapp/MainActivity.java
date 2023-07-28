//****************************************************//
// Project Three - Option #3 Weight Tracking App      //
// Sarah C Jodrey                                     //
// CS-360-H7351:Mobile Architecture and Programming   //
// December 11, 2022                                  //
//****************************************************//

package com.learn.weighttrackerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.Permissions;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WeightDatabase mWeightDB;
    private UserDao mUserDao;
    private User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create a single instance of the database
        mWeightDB = WeightDatabase.getInstance(getApplicationContext());
        mUserDao = mWeightDB.userDao();

    }

    //Called when user selects login button
    public void userLogin(View view){

        String username = ((EditText) findViewById(R.id.usernameText)).getText().toString();
        String password = ((EditText)  findViewById(R.id.passwordText)).getText().toString();

        if (username.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please complete all fields.", Toast.LENGTH_LONG).show();
        } else{
            //Call method to check if user exists in DB
            Boolean confirmUser = confirmLogin(username,password);
            if(confirmUser){
                mUser = new User(username, password);
                startWeightActivityMain();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Called when user selects create new user button
    public void registerNewUser(View view){
        String username = ((EditText) findViewById(R.id.usernameText)).getText().toString();
        String password = ((EditText)  findViewById(R.id.passwordText)).getText().toString();

        if (!username.isEmpty() && !password.isEmpty()){
            List<User> userList = mUserDao.getUsers();
            boolean userExists = false;

            //Check user list for existing username
            if (userList.size() > 0){
                for(int i = 0; i < userList.size(); i++){
                    if(userList.get(i).getUsername().equals(username)){userExists = true;}
                }
            }
            //If user is not found, create new account
            if (!userExists) {
                mUserDao.insertUser(new User(username, password));
                Toast.makeText(this, "New user created!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Username already exists.",Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this,"Please complete all fields.", Toast.LENGTH_LONG).show();
        }


    }

    //Bring user to weight tracking activity screen
    public void startWeightActivityMain(){
        Intent intent = new Intent(this, WeightTrackingMainActivity.class);
        intent.putExtra("user", mUser);
        startActivity(intent);
    }

    //Confirm user exists or return false
    private boolean confirmLogin(String username, String password){
        List<User> userList = mUserDao.getUsers();
        //cycle through list to find user
        for(int i = 0; i < userList.size(); i++){
            if(userList.get(i).getUsername().equals(username) &&
                    userList.get(i).getPassword().equals(password)){
                return true;
            }
        } return false;
    }


}