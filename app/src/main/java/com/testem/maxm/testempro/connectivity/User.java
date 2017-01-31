package com.testem.maxm.testempro.connectivity;

import java.io.Serializable;

/**
 * Created by Mr_95 on Jan 24, 2017.
 */

public final class User implements Serializable{

    private static final long serialVersionUID = 23109753;

    private Integer id;
    private String name;
    private String surname;
    private String secondName;
    private String institute;
    private String caf;
    private String email;
    private String password;
    private String tests;
    private String subjects;
    private String cellNumber;
    private String deviceID;

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

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getInstitute() {
        return institute;
    }

    public String getCaf() {
        return caf;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getTests() {
        return tests;
    }

    public String getSubjects() {
        return subjects;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public String getDeviceID() {
        return deviceID;
    }
}
