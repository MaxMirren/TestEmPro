package com.testem.maxm.testempro.inapp;

import com.testem.maxm.testempro.AuthActivity;
import com.testem.maxm.testempro.connectivity.ServerInterface;

import android.app.Activity;
import android.content.Intent;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_space);
        if (getIntent().getBooleanExtra("Close this app", false)){
            finish();
            return;
        }
        workSpace = this;
        userCacheChecker();
    }

    /**
     * Check if it is important to have a sign in
     * @return result of file being and content
     */
    public void userCacheChecker() {
        readUserFromFile();
        if (ServerInterface.currentUser != null) {
            if (ServerInterface.currentUser.deviceID.equals(getDeviceId())) {
                makeToast("Yeaaaaaah");
                setUpSuccessfulCheckResult();
            }
            else {
                makeToast("Server: " + ServerInterface.currentUser.deviceID + " real is: " + getDeviceId());
                setUpUnsuccessfulCheckResult();
            }
        }
        else {
            //makeToast("currentUser = null");
            setUpUnsuccessfulCheckResult();
        }
    }

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
           setUpUnsuccessfulCheckResult();
        }
       catch (ClassNotFoundException e) {
           makeToast(e.getMessage());
        }
    }

    public void setUpSuccessfulCheckResult () {
        this.getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ServerInterface.authActivity.finish();
        connectVariablesToViews();
    }

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
     * Gets caller-activity data
     */
    private void restoreIntent() {
        Intent intent = getIntent();
        textView.setText(intent.getStringExtra(ServerInterface.NAME));
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
