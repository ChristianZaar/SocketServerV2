package com.example.happening;

import java.io.Serializable;

/**
 * Comment info
 */
public class Comment implements Serializable {

    private int happeningId;
    private String userName;
    private String comment;
    private String date;
    private String time;

    public Comment(String userName, String comment, String date, String time) {
        this(0,userName,comment,date,time);
    }
    public Comment(int happeningId, String userName, String comment, String date, String time) {
        this.happeningId = happeningId;
        this.userName = userName;
        this.comment = comment;
        this.date = date;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getHappeningId() {
        return happeningId;
    }

    public void setHappeningId(int happeningId) {
        this.happeningId = happeningId;
    }
}
