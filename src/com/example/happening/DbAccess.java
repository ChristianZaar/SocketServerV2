package com.example.happening;

import com.mysql.jdbc.Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class DbAccess {
    private boolean tryReconnect;
    private Connection connection;
    String connectionURL;
    private boolean nameTested;//Is name in database if not try to add once

    public DbAccess(String connectionURL) {
        this.connectionURL = connectionURL;
        connector();
    }

    private final void connector() {
        try {
            connection = (Connection) DriverManager.getConnection(connectionURL);
            if(connection!=null) {
                connection.setAutoReconnect(true);
                tryReconnect = true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private boolean checkConnection(){
        if(connection!=null){
            try {
                if (!connection.isClosed()) {
                    if(connection.isValid(10)) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(tryReconnect){
            tryReconnect = false;
            connector();
            if(checkConnection()){
                return true;
            }
        }
        return false;
    }

    public void closeConnection(){
        try {
            if (connection != null)
                connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Gets Eventlist from db
     * @param city
     * @return
     */
//    public ArrayList<Happening> getEvents(String city){
//
//        ArrayList<Happening> list = new ArrayList<Happening>();
//        String query = "SELECT event.idEvent, event.user_iduser, event.city, user.name, event.date, event.description\n" +
//                "FROM event\n" +
//                "INNER JOIN user ON event.user_iduser = user.iduser where city = ?;";
//        boolean  retVal = false;
//
//        if (checkConnection()) {
//            try (PreparedStatement pst = connection.prepareStatement(query)) {
//
//                pst.setString(1, city);
//                ResultSet rs = pst.executeQuery();
//
//                while (rs.next()) {
//                    list.add(new Happening(
//                                rs.getInt(1),
//                                rs.getInt(2),
//                                rs.getString(3),
//                                rs.getString(4),
//                                rs.getString(5),
//                                rs.getString(6)));
//                }
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//
//            }
//        }
//        return list;
//    }


    /**
     * Adds event to database
     * @param happening
     */
    public void addHappening(Happening happening){
        //String query = "INSERT INTO happening.happening (user_iduser, city, date, description) VALUES (?, ?, ?, ?);";
        //INSERT INTO happening.happening (user_iduser,date, name, time, city, description) select user.iduser,'Hossleholm','now','s' from user where name='test';
        String query = "INSERT INTO happening.happening (user_iduser, name, date, time, city, description) select user.iduser,?,?,?,?,? from happening.user where name=?";
        boolean  retVal = false;
        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                pst.setString(1, happening.getName());
                pst.setString(2, happening.getDate());
                pst.setString(3, happening.getTime());
                pst.setString(4, happening.getCity());
                pst.setString(5, happening.getDescription());
                pst.setString(6, happening.getUserName());
                pst.execute();
                if( pst.getGeneratedKeys().getRow()== 0 && !nameTested){
                    if(!checkUser(happening.getUserName())){
                        addHappening(happening);
                    }

                }

            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        }
    }

    private boolean checkUser(String name) {
        nameTested = true;
        String query = "SELECT user.iduser from happening.user where user.name = ?";

        boolean exists = false;

        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1, name);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    exists = true;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();

            }
            if(!exists){
                addUser(name);
            }
        }
        return exists;
    }

    public void addUser(String name){
        String query = "INSERT INTO happening.user (name) VALUES (?);";
        boolean  retVal = false;
        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query)) {

                pst.setString(1, name);
                pst.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        }
    }
}
