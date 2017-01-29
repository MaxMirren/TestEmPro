package com.testem.maxm.testempro.connectivity;

import com.testem.maxm.testempro.AuthActivity;
import com.testem.maxm.testempro.inapp.Cacher;
import com.testem.maxm.testempro.inapp.WorkSpace;
import com.testem.maxm.testempro.connectivity.Functions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Mr_95 on Jan 29, 2017.
 */

public final class ServerInterface extends AsyncTask<String,String,String> {

    public static final String NAME = "NAME";

    private Connection con;
    public static AuthActivity authActivity;

    final String USERNAME = "testemadmin";
    final String PASSWORD = "su";
    final String DATABASE = "testem";
    final String IP = "5.101.194.75";

    public String info = "";
    public String email, password;
    private Boolean isSuccess = false;
    public Functions followingFunction;

    public static User currentUser;

    @Override
    protected String doInBackground(String... params)
    {
        switch (followingFunction) {
            case AUTHENTIFIER:
                checkLogIn();
                break;
            case REPORT_END:
                break;
            default:
                break;
        }
        return info;
    }

    @SuppressLint("NewApi")
    public Connection connectionclass(String user, String password, String database, String server)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + server + ";databaseName=" + database + ";user=" + user+ ";password=" + password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            info = se.getMessage().toString();
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            info = e.getMessage().toString();
            Log.e("error here 2 : ", e.getMessage());
        }
        catch (Exception e)
        {
            info = e.getMessage().toString();
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }


    private String checkLogIn () {
        try
        {
            con = connectionclass(USERNAME, PASSWORD, DATABASE, IP);        // Connect to database
            if (con == null)
            {
                info = "Check Your Internet Access!";
            }
            else
            {
                String query = "select * from teachers_accounts where email= '" + email +"' AND password= '" + password + "'";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                if(rs.next())
                {
                    createUser(rs);
                    Cacher cacher = new Cacher();
                    cacher.execute();
                    info = "Hello, " + currentUser.name;
                    isSuccess=true;
                    reportSessionStarted();
                }
                else
                {
                    info = "Invalid Credentials!";
                    isSuccess = false;
                }
            }
        }
        catch (Exception ex)
        {
            isSuccess = false;
            info = ex.getMessage().toString() + "  ";
            info = ex.toString();
        }
        return null;
    }

    private void createUser(ResultSet resultSet) {
        try {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String surname = resultSet.getString("surname");
            String secondName = resultSet.getString("second_name");
            String institute = resultSet.getString("institute");
            String caf = resultSet.getString("caf");
            String tests = resultSet.getString("tests");
            String subjects = resultSet.getString("subjects");
            String cellNumber = resultSet.getString("cell_number");
            currentUser = new User(id, name, surname, secondName, institute, caf, email, password, tests, subjects, cellNumber, authActivity.getDeviceId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String reportSessionStarted () {
        try
        {
            String query = "INSERT INTO report_teachers (teacher_id, surname, institute, caf, time, action, device) VALUES ('"  +
                    currentUser.id + "', N'" + currentUser.surname + "', N'" + currentUser.institute + "', N'" + currentUser.caf +
                    "', '" + getDateTime() + "', 'Sign IN', '" + currentUser.deviceID + "')";
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        }
        catch (Exception ex)
        {
            info = ex.getMessage().toString() + "  ";
        }
        return null;
    }

    private String reportSessionEnded() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            info = e.getMessage().toString();
        }
        return null;
    }


    public void sendData (AuthActivity activity, String mail, String passwd) {
        authActivity = activity;
        email = mail;
        password = passwd;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    protected void onPostExecute(String s) {
        switch (followingFunction) {
            case AUTHENTIFIER:
                authActivity.makeToast(info);
                Intent intent = new Intent(authActivity, WorkSpace.class);
                intent.putExtra(NAME, currentUser.name);
                authActivity.startActivity(intent);
                break;
            case REPORT_END:
                if (info != "") {
                    authActivity.makeToast(info);
                }
                break;
            default:
                break;
        }

    }
}

