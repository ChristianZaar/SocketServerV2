package com.example.happening.DbStuff;

import java.io.Serializable;

/**
 * Used for parameters when asking server for happenings
 */
public class GetHappeningsRequest implements Serializable{
    private String userName;
    private String dateStart;
    private String dateEnd;

    public GetHappeningsRequest(String userName, String dateStart, String dateEnd) {
        this.userName = userName;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public String getUserName() {
        return userName;
    }

    public String getDateStart() {
        return dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }
}
