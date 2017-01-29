package com.testem.maxm.testempro.inapp;

import com.testem.maxm.testempro.connectivity.ServerInterface;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.testem.maxm.testempro.R;

/**
 * Created by Mr_95 on Jan 29, 2017.
 */

public final class WorkSpace extends AppCompatActivity {

    TextView textView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_space);
        connectVariablesToViews();
        restoreIntent();
        ServerInterface.authActivity.finish();
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

}
