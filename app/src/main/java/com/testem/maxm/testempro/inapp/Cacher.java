package com.testem.maxm.testempro.inapp;

import android.content.Context;
import android.os.AsyncTask;

import com.testem.maxm.testempro.connectivity.ServerInterface;
import com.testem.maxm.testempro.connectivity.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class Cacher extends AsyncTask <Void, Void, Void> {

    private static String information = "";

    public static boolean isReading = true;

    @Override
    protected Void doInBackground(Void... params) {
        if (isReading) {
            readUserFromFile();
        }
            else {
            writeUserToFile();
        }
        return null;
    }

    private void writeUserToFile() {
        try {
            FileOutputStream fileOutputStream = ServerInterface.authActivity.openFileOutput("user.tep", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(ServerInterface.currentUser);
            objectOutputStream.flush();
            objectOutputStream.close();
            fileOutputStream.close();
            //information = "User successfully cached!";
        }
        catch (IOException e) {
            information = e.getMessage();
        }
    }


    private void readUserFromFile() {
        try {
            FileInputStream fileInputStream = ServerInterface.authActivity.openFileInput("user.tep");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            ServerInterface.currentUser = (User) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch (IOException e) {
            information = e.getMessage();
        }
       catch (ClassNotFoundException e) {
            information = e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (!information.equals("")) {
            ServerInterface.authActivity.makeToast(information);
        }
    }
}
