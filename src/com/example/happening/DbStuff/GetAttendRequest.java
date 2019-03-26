package com.example.happening.DbStuff;

import java.io.Serializable;

/**
 * Data for Attend request
 */
public class GetAttendRequest implements Serializable {
    private String name;
    private int happeningId;

    public GetAttendRequest(String name, int happeningId) {
        this.name = name;
        this.happeningId = happeningId;
    }

    public String getName() {
        return name;
    }

    public int getHappeningId() {
        return happeningId;
    }
}
