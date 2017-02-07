package com.testem.maxm.testempro.inapp.tabs;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.testem.maxm.testempro.R;

public final class DevelopingTests extends Fragment {

    private View view;
    private FloatingActionButton fabAdd, fabAddTest, fabDownloadExcel;
    private Animation fabOpen, fabClose, fabRotateUp, fabRotateDown;
    private boolean floatingActionButtonPressed = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_developing_tests, container, false);
        setFloatingActionButtons();
        return view;
    }

    /**
     * Sets the actions when main floating button is clicked
     */
    private void setFloatingActionButtons () {
        fabAdd = (FloatingActionButton) view.findViewById(R.id.main_add);
        fabAddTest = (FloatingActionButton) view.findViewById(R.id.add_test);
        fabDownloadExcel = (FloatingActionButton) view.findViewById(R.id.download_excel);
        fabOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fabRotateUp = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_rotate_up);
        fabRotateDown = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_rotate_down);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatingActionButtonPressed) {
                    fabAdd.startAnimation(fabRotateDown);
                    fabAddTest.startAnimation(fabClose);
                    fabDownloadExcel.startAnimation(fabClose);
                    fabAddTest.setClickable(false);
                    fabDownloadExcel.setClickable(false);
                    floatingActionButtonPressed = false;
                }
                else {
                    fabAdd.startAnimation(fabRotateUp);
                    fabAddTest.startAnimation(fabOpen);
                    fabDownloadExcel.startAnimation(fabOpen);
                    fabAddTest.setClickable(true);
                    fabDownloadExcel.setClickable(true);
                    floatingActionButtonPressed = true;
                }
            }
        });
    }

}
