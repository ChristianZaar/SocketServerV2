package com.example.happening;

import java.io.Serializable;

public class Happening implements Serializable{



    private String userName;
    private String name;
    private String date;
    private String time;
    private String city;
    private String description;

    public Happening(String name, String date, String time, String city, String description) {
        this("", name, date, time, city, description);
    }

    public Happening(String userName, String name, String date, String time, String city, String description) {
        this.userName = userName;
        this.name = name;
        this.date = date;
        this.time = time;
        this.city = city;
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }
}
