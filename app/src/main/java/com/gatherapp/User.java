package com.gatherapp;

import java.util.ArrayList;

/**
 * Created by Gino on 10/21/2016.
 */

public class User {

    String id;
    String name;
    String gender;
    String email;
    String birthday;
    String location;
    String contactNum;

    public User() {
    }

    public User(String id, String name, String gender, String email, String birthday, String location, String contactNum) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.birthday = birthday;
        this.location = location;
        this.contactNum = contactNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

}
