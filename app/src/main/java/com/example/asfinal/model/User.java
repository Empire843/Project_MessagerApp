package com.example.asfinal.model;

import java.io.Serializable;

public class User implements Serializable  {
    private String uid;
    private String full_name;
    private String phone_number;
    private String email;
    private String password;

    public User() {
    }

    public User(String uid, String full_name, String phone_number, String email, String password) {
        this.uid = uid;
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
    }

    public User(String full_name, String phone_number, String email, String password) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String id) {
        this.uid = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String gender) {
        this.phone_number = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
