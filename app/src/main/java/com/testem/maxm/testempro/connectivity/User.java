package com.testem.maxm.testempro.connectivity;

import java.io.Serializable;

/**
 * Created by Mr_95 on Jan 24, 2017.
 */

public final class User implements Serializable{

    private static final long serialVersionUID = 23109753;

    public Integer id;
    public String name;
    public String surname;
    public String secondName;
    public String institute;
    public String caf;
    public String email;
    public String password;
    public String tests;
    public String subjects;
    public String cellNumber;
    public String deviceID;

    public User(Integer id, String name, String surname, String secondName, String institute, String caf, String email, String password, String tests, String subjects, String cellNumber, String deviceID) {
        this.id = id;
        this.name = name.replaceAll(" ", "");
        this.surname = surname.replaceAll(" ", "");
        this.secondName = secondName.replaceAll(" ", "");
        this.institute = institute;
        this.caf = caf;
        this.email = email.replaceAll(" ", "");
        this.password = password.replaceAll(" ", "");
        this.tests = tests;
        this.subjects = subjects;
        this.cellNumber = cellNumber;
        this.deviceID = deviceID;
    }


}
