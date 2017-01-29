package com.testem.maxm.testempro.connectivity;

/**
 * Created by Mr_95 on Jan 24, 2017.
 */

public final class User {
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
