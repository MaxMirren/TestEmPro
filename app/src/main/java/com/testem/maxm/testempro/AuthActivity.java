package com.testem.maxm.testempro;

import com.testem.maxm.testempro.connectivity.ServerInterface;
import com.testem.maxm.testempro.connectivity.Functions;
import com.testem.maxm.testempro.inapp.WorkSpace;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthActivity extends AppCompatActivity {

    private static final String EMAIL = "EMAIL";                            //code for email field data in instance state
    private static final String PASSWORD = "PASSWORD";                      //code for password field data in instance state
    private static final String SIGN_IN_ENABLED = "SIGN_IN_ENABLED";        //code for enable state of sign in button in instance state

    private EditText emailEditText;                                         //relative of email field
    private EditText passwordEditText;                                      //relative of password field
    private Button signIn;                                                  //relative of sign in button

    private static String emailTyped = "";                                  //email field data for instance state
    private static String passwordTyped = "";                               //password field data for instance state
    private static Boolean signInEnabled = false;                           //sign in button data for instance state

    private  ServerInterface serverInterface;                               //a way to use object's methods
    //private GoogleApiClient client;                                         //represents google api interaction


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);
        connectVariablesToViews();
        listenToFields();
    }


    /**
     * Prevents going to the previous activity when it is needed
     */
    @Override
    public void onBackPressed() {
            Intent intent = new Intent(this, WorkSpace.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Close this app", true);
            startActivity(intent);
            finish();
    }

    /**
     * Connects on-form objects to in-class variables
     */
    private void connectVariablesToViews() {
        emailEditText = (EditText) findViewById(R.id.emailField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        signIn = (Button) findViewById(R.id.sign_in);
    }

    /**
     * Saves changeable data when state is changed
     * @param outState represents current state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EMAIL, emailTyped);
        outState.putString(PASSWORD, passwordTyped);
        outState.putBoolean(SIGN_IN_ENABLED, signInEnabled);
    }

    /**
     * Restores changeable data when state is changed
     * @param savedInstanceState represents daved state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        emailTyped = savedInstanceState.getString(EMAIL);
        passwordTyped = savedInstanceState.getString(PASSWORD);
        signInEnabled = savedInstanceState.getBoolean(SIGN_IN_ENABLED);
        emailEditText.setText(emailTyped);
        passwordEditText.setText(passwordTyped);
        signIn.setEnabled(signInEnabled);
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
            makeToast("There is no internet connection");
            return false;
        }
    }

    /**
     * Listens to email and password fields to save data and change the state of sign in button
     */
    private void listenToFields() {
        emailEditText.addTextChangedListener(new TextWatcher() {

            /**
             * This is auto-generated method that does something before listened fields' text changed
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * This is auto-generated method that does something when listened fields' text changed
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailTyped = emailEditText.getText().toString();
                if ((emailTyped.isEmpty()) || (passwordTyped.isEmpty())) {
                    signInEnabled = false;
                    signIn.setEnabled(signInEnabled);
                } else {
                    signInEnabled = true;
                    signIn.setEnabled(signInEnabled);
                }
            }

            /**
             * This is auto-generated method that does something after listened fields' text changed
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordTyped = passwordEditText.getText().toString();
                if ((emailTyped.isEmpty()) || (passwordTyped.isEmpty())) {
                    signInEnabled = false;
                    signIn.setEnabled(signInEnabled);
                } else {
                    signInEnabled = true;
                    signIn.setEnabled(signInEnabled);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * Sets up method when a view was clicked (Calls async task to authenticate user via SQL Server)
     * @param view represents element that was clicked
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                if (hasInternetConnection()) {
                    serverInterface = new ServerInterface();
                    serverInterface.getAuthActivityAndTypedData(this, emailTyped, passwordTyped);
                    serverInterface.followingFunction = Functions.AUTHENTIFIER;
                    serverInterface.execute("");
                }
                else {
                    break;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Simplifies the notification system by doing the same with simple interface
     * @param string incoming data user notified of
     */
    public void makeToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

    /**
     * Gets device unique number
     * @return obtained device number
     */
    public String getDeviceId() {
        //TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String number = Build.SERIAL;
        return number;
    }
}

