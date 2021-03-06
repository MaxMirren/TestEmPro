package com.testem.maxm.testempro.inapp;

import com.testem.maxm.testempro.AuthActivity;
import com.testem.maxm.testempro.connectivity.Functions;
import com.testem.maxm.testempro.connectivity.ServerInterface;
import com.testem.maxm.testempro.inapp.fileChooser.FileChooserActivity;
import com.testem.maxm.testempro.inapp.tabs.*;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.testem.maxm.testempro.R;
import com.testem.maxm.testempro.connectivity.User;
import com.testem.maxm.testempro.inapp.tabs.ViewPagerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


public final class WorkSpace extends AppCompatActivity {


    public static final String CURRENT_TAB = "CURRENT_TAB";
    public static WorkSpace workSpace;
    public static Boolean authorizationCompleted = false;

    private ServerInterface serverInterface;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView userNameAtDrawerHeader;
    private TextView cafNameAtDrawerHeader;
    private View header;

    public int currentTab = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_space);
        if (getIntent().getBooleanExtra("Close this app", false)){
            finish();
            return;
        }
        workSpace = this;
        //if this is not the first Sign In right after the Log in and if this user was not checked
        if (!ServerInterface.signIn && !authorizationCompleted) {
            userCacheChecker();
        }
        else {
            setUpSuccessfulCheckResult();
        }
        if (!authorizationCompleted) {
            setUpUnsuccessfulCheckResult();
        }
    }

    /**
     * Saves changeable data when state is changed
     * @param outState represents current state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_TAB, viewPager.getCurrentItem());
    }

    /**
     * Restores changeable data when state is changed
     * @param savedInstanceState represents daved state
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(CURRENT_TAB), false);
    }

    /**
     * Checks if it is important to have a sign in
     *
     */
    public void userCacheChecker() {
            readUserFromFile();
            if (ServerInterface.currentUser != null) {
                if (ServerInterface.currentUser.getDeviceID().equals(getDeviceSerialNumber())) {
                    makeToast("Yeaaaaaah");
                    setUpSuccessfulCheckResult();
                }
                else {
                    ServerInterface.signIn = true;
                    makeToast("Server: " + ServerInterface.currentUser.getDeviceID() + " real is: " + getDeviceSerialNumber());
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
        if (hasInternetConnection() && !ServerInterface.signIn && !authorizationCompleted)  {
            serverInterface = new ServerInterface();
            serverInterface.followingFunction = Functions.AUTHENTIFIER_CACHE;
            serverInterface.execute();
        }
        authorizationCompleted  = true;
        connectVariablesToViews();
        setUserDataUI();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_work_space);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.work_space_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        header = ((NavigationView) findViewById(R.id.navigation_view)).getHeaderView(0);
        userNameAtDrawerHeader = (TextView) header.findViewById(R.id.user_name_and_etc);
        cafNameAtDrawerHeader = (TextView) header.findViewById(R.id.caf_name);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new HistoryTests(), getResources().getString(R.string.history_tests));
        viewPagerAdapter.addFragments(new DevelopingTests(), getResources().getString(R.string.developing_tests));
        viewPagerAdapter.addFragments(new UpcomingTests(), getResources().getString(R.string.upcoming_tests));
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1, false);

    }

    /**
     * Describes the action to complete when the option item is selected
     * @param item item selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    public String getDeviceSerialNumber() {
        //TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String number = Build.SERIAL;
        return number;
    }

    /**
     * Simplifies the notification system by doing the same with simple interface
     * @param string incoming data user notified of
     */
    public void makeToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

    /**
     * Fills up UI content with user's content
     */
    private void setUserDataUI () {
        String surname = ServerInterface.currentUser.getSurname() + " " + ServerInterface.currentUser.getName().charAt(0)
                + "." + ServerInterface.currentUser.getSecondName().charAt(0) + ".";
        String cafName = ServerInterface.currentUser.getCaf();
        String[] cafWords = cafName.split(" ");
        cafName = "";
        for (String word : cafWords) {
            if (word.length() > 1) {
                cafName += word + " ";
            }
        }
        if (cafName.length() > 19) {
            cafName = "";
            for (String word : cafWords) {
                if (word.length() > 1) {
                    word = word.toUpperCase();
                    cafName += word.charAt(0)+".";
                }
            }
        }
        userNameAtDrawerHeader.setText(surname);
        cafNameAtDrawerHeader.setText(cafName);
    }

    /**
     * Listens to events and completes deeds
     * @param view refers to the view was called this method
     */
    public void eventListener (View view) {
        switch (view.getId()) {
            case R.id.sign_out_button:
                authorizationCompleted = false;
                ServerInterface.signIn = false;
                Cacher cacher = new Cacher();
                cacher.followingFunction = CacherFunctions.DELETE_USER_DATA;
                cacher.execute();
                if (hasInternetConnection()) {
                    serverInterface = new ServerInterface();
                    serverInterface.followingFunction = Functions.REPORT_SIGN_OUT;
                    serverInterface.execute();
                }
                setUpUnsuccessfulCheckResult();
                finish();
                break;
            case R.id.download_excel:
                Intent intent = new Intent(this, FileChooserActivity.class);
                this.startActivity(intent);
                break;
            default:
                break;
        }
    }

   private static void readExcelFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.w("FileUtils", "Storage not available or read only");
            return;
        }

        try{
            // Creating Input Stream
            File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator<Row> rowIter = mySheet.rowIterator();

            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();
                while(cellIter.hasNext()){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.w("FileUtils", "Cell Value: " +  myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){e.printStackTrace(); }

        return;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
