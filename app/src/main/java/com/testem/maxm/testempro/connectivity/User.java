package com.testem.maxm.testempro.connectivity;

import android.content.Context;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Mr_95 on Jan 24, 2017.
 */

public final class User implements Serializable{
    Integer id;
    String name;
    String surname;
    String secondName;
    String institute;
    String caf;
    String email;
    String password;
    String tests;
    String subjects;
    String cellNumber;
    String deviceID;

    public User(Integer id, String name, String surname, String secondName, String institute, String caf, String email, String password, String tests, String subjects, String cellNumber, String deviceID) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.secondName = secondName;
        this.institute = institute;
        this.caf = caf;
        this.email = email;
        this.password = password;
        this.tests = tests;
        this.subjects = subjects;
        this.cellNumber = cellNumber;
        this.deviceID = deviceID;
    }


}
