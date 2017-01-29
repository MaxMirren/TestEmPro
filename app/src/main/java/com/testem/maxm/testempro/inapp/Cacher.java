package com.testem.maxm.testempro.inapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.constraint.solver.Cache;

import com.testem.maxm.testempro.connectivity.ServerInterface;
import com.testem.maxm.testempro.connectivity.User;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Mr_95 on Jan 29, 2017.
 */

public final class Cacher extends AsyncTask <Void, Void, Void> {

    private static String information = "";
    User user;

    @Override
    protected Void doInBackground(Void... params) {
        writeUserToFile();
        return null;
    }

    public void writeUserToFile() {
        File file = new File(ServerInterface.authActivity.getCacheDir(), "user.tep");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream fileOutputStream = ServerInterface.authActivity.openFileOutput("user.tep", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(ServerInterface.currentUser);
            objectOutputStream.close();
            fileOutputStream.close();
            information = "User successfully cached!";
        }
        catch (IOException e) {
            information = e.getMessage().toString();
        }
    }


    public void readUserFromFile() {
        try {
            FileInputStream fileInputStream = ServerInterface.authActivity.openFileInput("user.tep");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            user = (User) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch (IOException e) {
            information = e.getMessage().toString();
        }
        catch (ClassNotFoundException e) {
            information = e.getMessage().toString();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        ServerInterface.authActivity.makeToast(information);
    }
}
