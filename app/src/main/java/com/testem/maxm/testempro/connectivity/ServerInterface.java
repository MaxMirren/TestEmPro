package com.testem.maxm.testempro.connectivity;

import com.testem.maxm.testempro.AuthActivity;
import com.testem.maxm.testempro.inapp.Cacher;
import com.testem.maxm.testempro.inapp.CacherFunctions;
import com.testem.maxm.testempro.inapp.WorkSpace;

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

public final class ServerInterface extends AsyncTask<String,String,String> {

    final private static String USERNAME = "testemadmin";               //SQL Connection Data - name
    final private static String PASSWORD = "su";                        //SQL Connection Data - password
    final private static String DATABASE = "testem";                    //SQL Connection Data - database
    final private static String IP = "5.101.194.75";                    //SQL Connection Data - IPV4 address of server

    private Connection con;                                             //SQL Connection Data transferring variable
    public Functions followingFunction;                                 //Determines which function is needed to be completed
    public static User currentUser;                                     //Determines all fields of current user
    public static Boolean signIn = false;                               //Determines if there was the first sign in into application
    private static String info = "";                                    //Contents the information about SQL status queries

    public static AuthActivity authActivity;                            //Reference to the AuthActivity main element
    private static String email;                                        //Represents typed email in AuthActivity
    private static String password;                                     //Represents typed password in AuthActivity


    /**
     * The main function after execute: determines which function is going to be completed
     * @param params string
     * @return null
     */
    @Override
    protected String doInBackground(String... params)
    {
        switch (followingFunction) {
            case AUTHENTIFIER:
                checkLogIn();
                break;
            case AUTHENTIFIER_CACHE:
                reportSessionStartedFromCache();
                break;
            case REPORT_SIGN_OUT:
                reportSignOut();
                break;
            default:
                break;
        }
        return null;
    }

    /**
     *
     * @param user SQL Connection Data - name
     * @param password SQL Connection Data - password
     * @param database SQL Connection Data - database
     * @param server SQL Connection Data - IPV4 address of server
     * @return connection status data
     */
    @SuppressLint("NewApi")
    public Connection connectionClass(String user, String password, String database, String server)
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

    /**
     * Checks if email and password are typed properly and then completes following functions
     * to create new user if there was a match
     * @return null
     */
    private String checkLogIn () {
        try
        {
            con = connectionClass(USERNAME, PASSWORD, DATABASE, IP);        // Connect to database
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
                    //authActivity.signIn.setEnabled(false);
                    info = "Hello, " + currentUser.getName();
                    reportSessionStarted();
                }
                else
                {
                    info = "Invalid Credentials!";
                }
            }
        }
        catch (Exception ex)
        {
            info = ex.getMessage().toString() + "  ";
            info = ex.toString();
        }
        return null;
    }

    /**
     * Creates new user to work with and caches object's data to file
     * @param resultSet
     */
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
            Cacher cacher = new Cacher();
            cacher.followingFunction = CacherFunctions.WRITE_USER_DATA;
            cacher.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a record to report_teachers.dbo with session's start data content
     * @return null
     */
    private String reportSessionStarted () {
        try
        {
            String query = "INSERT INTO report_teachers (teacher_id, surname, institute, caf, time, action, device) VALUES ('"  +
                    currentUser.getId() + "', N'" + currentUser.getSurname() + "', N'" + currentUser.getInstitute() + "', N'" + currentUser.getCaf() +
                    "', '" + getDateTime() + "', 'SIGN_IN', '" + currentUser.getDeviceID() + "')";
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        }
        catch (Exception ex)
        {
            info = ex.getMessage().toString() + "  ";
        }
        return null;
    }

    /**
     * Adds a record to report_teachers.dbo with sign out data content
     * @return null
     */
    private String reportSignOut() {
        con = connectionClass(USERNAME, PASSWORD, DATABASE, IP);        // Connect to database
        if (con == null)
        {
            info = "Check Your Internet Access!";
        }
        else {
            try
            {
                String query = "INSERT INTO report_teachers (teacher_id, surname, institute, caf, time, action, device) VALUES ('"  +
                        currentUser.getId() + "', N'" + currentUser.getSurname() + "', N'" + currentUser.getInstitute() + "', N'" + currentUser.getCaf() +
                        "', '" + getDateTime() + "', 'SIGN_OUT', '" + currentUser.getDeviceID() + "')";
                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);
                currentUser = null;
            }
            catch (Exception ex)
            {
                info = ex.getMessage().toString() + "  ";
            }
        }
        return null;
    }

    /**
     * Adds a record to report_teachers.dbo with session's (app-launching) data content
     * @return null
     */
    private void reportSessionStartedFromCache() {
        con = connectionClass(USERNAME, PASSWORD, DATABASE, IP);        // Connect to database
        if (con == null)
        {
            info = "Check Your Internet Access!";
        }
        else {
            try
            {
                String query = "INSERT INTO report_teachers (teacher_id, surname, institute, caf, time, action, device) VALUES ('"  +
                        currentUser.getId() + "', N'" + currentUser.getSurname() + "', N'" + currentUser.getInstitute() + "', N'" + currentUser.getCaf() +
                        "', '" + getDateTime() + "', 'LAUNCH', '" + currentUser.getDeviceID() + "')";
                Statement stmt = con.createStatement();
                stmt.executeUpdate(query);
            }
            catch (Exception ex)
            {
                info = ex.getMessage().toString() + "  ";
            }
        }
    }

    /**
     * Interface to get AuthActivity data
     * @param activity AuthActivity.this object
     * @param mail typed in field email
     * @param passwd typed in filed password
     */
    public void getAuthActivityAndTypedData(AuthActivity activity, String mail, String passwd) {
        authActivity = activity;
        email = mail;
        password = passwd;
    }

    /**
     * Gets current exact date and time
     * @return formatted date and time
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /** Completes and ends up functions dependently on previously selected following function
     * @param s
     */
    @Override
    protected void onPostExecute(String s) {
        switch (followingFunction) {
            case AUTHENTIFIER:
                authActivity.makeToast(info);
                Intent intent = new Intent(authActivity, WorkSpace.class);
                authActivity.startActivity(intent);
                break;
            case AUTHENTIFIER_CACHE:

                break;
            case REPORT_SIGN_OUT:
                if (!info.equals("")) {
                    WorkSpace.workSpace.makeToast(info);
                }
                break;
            default:
                break;
        }

    }
}

