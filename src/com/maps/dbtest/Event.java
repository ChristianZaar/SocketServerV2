package com.maps.dbtest;

import java.io.Serializable;

public class Event implements Serializable {
    private int id;
    private int userId;
    private String city;
    private String user;
    private String date;
    private String description;


    public Event(int userId, String city, String user, String date, String description) {
        this(0,userId,city,user,date,date);
    }

    public Event(int id, int userId, String city, String user, String date, String description) {
        this.id = id;
        this.userId = userId;
        this.city = city;
        this.user = user;
        this.date = date;
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
