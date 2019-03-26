package com.example.happening;

import java.io.Serializable;

public class Happening implements Serializable {

    private int id;             //Id in DB for updates
    private String userName;    //creator
    private String name;        //title
    private String date;        //date
    private String time;        //time when happening happens :D
    private String city;        //Location
    private String description; //About
    private boolean attending;  //Attending

    public Happening(String userName,String name, String date, String time, String city, String description) {
        this(0,userName, name, date, time, city, description,true);
    }

    public Happening(int id, String userName, String name, String date, String time, String city, String description, boolean attending) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.date = date;
        this.time = time;
        this.city = city;
        this.description = description;
        this.attending = attending;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAttending() {
        return attending;
    }

    public void setAttending(boolean attending) {
        this.attending = attending;
    }
}
