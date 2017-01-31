package com.testem.maxm.testempro.inapp;

import com.testem.maxm.testempro.AuthActivity;
import com.testem.maxm.testempro.connectivity.Functions;
import com.testem.maxm.testempro.connectivity.ServerInterface;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.testem.maxm.testempro.R;
import com.testem.maxm.testempro.connectivity.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Mr_95 on Jan 29, 2017.
 */

public final class WorkSpace extends AppCompatActivity {

    TextView textView;
    public static WorkSpace workSpace;
    ServerInterface serverInterface;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_space);
        if (getIntent().getBooleanExtra("Close this app", false)){
            finish();
            return;
        }
        workSpace = this;
        if (!ServerInterface.signIn) {
            userCacheChecker();
        }
        else {
            setUpSuccessfulCheckResult();
        }
    }

    /**
     * Checks if it is important to have a sign in
     *
     */
    public void userCacheChecker() {
            readUserFromFile();
            if (ServerInterface.currentUser != null) {
                if (ServerInterface.currentUser.getDeviceID().equals(getDeviceId())) {
                    makeToast("Yeaaaaaah");
                    setUpSuccessfulCheckResult();
                }
                else {
                    ServerInterface.signIn = true;
                    makeToast("Server: " + ServerInterface.currentUser.getDeviceID() + " real is: " + getDeviceId());
                    setUpUnsuccessfulCheckResult();
                }
            }
            else {
                //makeToast("currentUser = null");
                ServerInterface.signIn = true;
                setUpUnsuccessfulCheckResult();
            }
    }

    /**
     * Checks if there a user.tep file and gets its object using deserialization
     */
    public void readUserFromFile() {
        try {
            FileInputStream fileInputStream = openFileInput("user.tep");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            ServerInterface.currentUser = new User(-1, "", "", "", "", "", "", "", "", "", "", "");
            ServerInterface.currentUser = (User) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch (IOException e) {
            //makeToast(e.getMessage());
            ServerInterface.signIn = true;
            setUpUnsuccessfulCheckResult();
        }
       catch (ClassNotFoundException e) {
           ServerInterface.signIn = true;
           makeToast(e.getMessage());
        }
    }

    /**
     * Set up if cache authentication was successful
     */
    public void setUpSuccessfulCheckResult () {
        if (hasInternetConnection() && !ServerInterface.signIn)  {
            serverInterface = new ServerInterface();
            serverInterface.followingFunction = Functions.AUTHENTIFIER_CACHE;
            serverInterface.execute();
        }
        connectVariablesToViews();
    }

    /**
     * Set up if cache authentication was unsuccessful
     */
    public void setUpUnsuccessfulCheckResult () {
        Intent intent = new Intent(this, AuthActivity.class);
        this.startActivity(intent);
    }

    /**
     * Connects on-form objects to in-class variables
     */
    private void connectVariablesToViews() {
        textView = (TextView) findViewById(R.id.textView);
    }

    /**
     * Checks if internet connection is available
     * @return the result of check
     */
    private Boolean hasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if ((networkInfo != null) && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets device unique number
     * @return obtained device number
     */
    public String getDeviceId() {
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String number = tm.getDeviceId();
        return number;
    }

    /**
     * Simplifies the notification system by doing the same with simple interface
     * @param string incoming data user notified of
     */
    public void makeToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }
}
