package com.example.happening;

import com.example.happening.DbStuff.GetAttendRequest;
import com.example.happening.DbStuff.ReturnValue;
import com.mysql.jdbc.Connection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DbAccess {

    private ReturnValue retVal = ReturnValue.GENERAL_FAILURE; //1 = Success, -1 = General failure, -2 = No connection to DB
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
        retVal = ReturnValue.NO_CONN_TO_DB;
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
     * @return
     */
    public ArrayList<ArrayList<Happening>> getHappenings(String userName,LocalDateTime startDate, LocalDateTime endDate){

        ArrayList<ArrayList<Happening>> list = new ArrayList<ArrayList<Happening>>();
        list.add(new ArrayList<Happening>());
        list.add(new ArrayList<Happening>());

        String query;

        query = "SELECT happening.idEvent,user.name, happening.name, happening.date_time, happening.city, happening.description, "
        +"CASE "
        +"WHEN exists( select happening_idEvent, attend.user_iduser from attend where happening_idEvent = happening.idEvent && attend.user_iduser = (select  user.iduser from user where user.name = ?) "
        +"|| happening.user_iduser = (select  user.iduser from user where user.name = ?)) THEN \"true\" "
        +"ELSE 0 "
        +"END AS attending "
        +"FROM happening.happening "
        +"INNER JOIN user ON happening.user_iduser = user.iduser "
        +"where happening.date_time > ? && happening.date_time <= ? order by happening.date_time";

        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query)) {
                pst.setString(1,userName);
                pst.setString(2,userName);
                pst.setTimestamp(3,Timestamp.valueOf(startDate));
                pst.setTimestamp(4,Timestamp.valueOf(endDate));

                ResultSet rs = pst.executeQuery();

                while (rs.next()) {

                    LocalDateTime d = rs.getTimestamp(4).toLocalDateTime();
                    String date = d.toLocalDate().toString();
                    String time = d.toLocalTime().toString();

                    Happening h = new Happening(
                            rs.getInt(1),       //Event id
                            rs.getString(2),    //User name
                            rs.getString(3),    //Title
                            date,                           //date
                            time,                           //time
                            rs.getString(5),    //City
                            rs.getString(6),    //Desc
                            Boolean.parseBoolean(rs.getString(7))); //Attending

                    if(h.isAttending()){
                        list.get(1).add(h);
                    }
                    else {
                        list.get(0).add(h);
                    }
                }
                retVal = ReturnValue.SUCCESS;
            } catch (SQLException ex) {
                ex.printStackTrace();
                retVal = ReturnValue.GENERAL_FAILURE;
            }
        }
        return list;
    }


    /**
     * Adds event to database
     * @param happening
     */
    public void addHappening(Happening happening){

        String query = "INSERT INTO happening.happening (user_iduser, name, date_time, city, description) select user.iduser,?,?,?,? from happening.user where name=?";

        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                pst.setString(1, happening.getName());
                pst.setTimestamp(2, Timestamp.valueOf(LocalDateTime.parse(happening.getDate()+"T"+happening.getTime())));
                pst.setString(3, happening.getCity());
                pst.setString(4, happening.getDescription());
                pst.setString(5, happening.getUserName());
                pst.execute();
                ResultSet rs = pst.getGeneratedKeys();

                boolean inserted = rs.next();//Has row been inserted?

                if( !inserted && !nameTested){
                    if(!checkUser(happening.getUserName())){
                        addHappening(happening);
                    }
                }
                else if(inserted){
                    retVal = ReturnValue.SUCCESS;
                }
                else{
                    retVal = ReturnValue.GENERAL_FAILURE; //Success
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                retVal = ReturnValue.GENERAL_FAILURE;
            }
        }
    }

    public void addAttend(GetAttendRequest attendRequest){

        String query = "INSERT INTO happening.attend (happening_idEvent, user_iduser)  select ?, user.iduser from user where user.name = ? ;";

        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                pst.setInt(1, attendRequest.getHappeningId());
                pst.setString(2, attendRequest.getName());
                int i = pst.executeUpdate();

                if( i<1 && !nameTested){
                    if(!checkUser(attendRequest.getName())){
                        addAttend(attendRequest);
                    }
                }
                else if(1 > 0){
                    retVal = ReturnValue.SUCCESS;
                }
                else{
                    retVal = ReturnValue.GENERAL_FAILURE; //Success
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                retVal = ReturnValue.GENERAL_FAILURE;
            }
        }
    }

    public void deleteAttend(GetAttendRequest attendRequest ){

        String query = "DELETE FROM attend WHERE `happening_idEvent`= ? and (select user.iduser from user where user.name = ?);";

        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                pst.setInt(1, attendRequest.getHappeningId());
                pst.setString(2, attendRequest.getName());
                int i = pst.executeUpdate();

                if(i > 0){
                    retVal = ReturnValue.SUCCESS;
                }
                else{
                    retVal = ReturnValue.GENERAL_FAILURE; //Success
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                retVal = ReturnValue.GENERAL_FAILURE;
            }
        }
    }

    public void addComment(Comment comment){

        String query = "INSERT INTO happening.comments (user_iduser, event_idEvent, comment, date_time) select iduser, ?, ?,now() from user where user.name = ?;";

        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

                pst.setInt(1, comment.getHappeningId());
                pst.setString(2, comment.getComment());
                pst.setString(3, comment.getUserName());
                int i = pst.executeUpdate();

                if( i<1 && !nameTested){
                    if(!checkUser(comment.getUserName())){
                        addComment(comment);
                    }
                }
                else if(1 > 0){
                    retVal = ReturnValue.SUCCESS;
                }
                else{
                    retVal = ReturnValue.GENERAL_FAILURE; //Success
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                retVal = ReturnValue.GENERAL_FAILURE;
            }
        }
    }

    public ArrayList<Comment> getComments(Happening happening){

        ArrayList<Comment> comments = new ArrayList<>();
        String query = "SELECT comments.event_idEvent , user.name, comments.comment, comments.date_time from comments INNER JOIN user ON comments.user_iduser = user.iduser where event_idEvent = ?;";

        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {


                pst.setInt(1, happening.getId());
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {

                    LocalDateTime d = rs.getTimestamp(4).toLocalDateTime();
                    String date = d.toLocalDate().toString();
                    String time = d.toLocalTime().toString();

                    Comment comment = new Comment(
                            rs.getInt(1),       //Event id
                            rs.getString(2),    //User name
                            rs.getString(3),    //Title
                            date,                           //date
                            time);                           //time//Attending
                    comments.add(comment);
                }
                retVal = ReturnValue.SUCCESS;
            } catch (SQLException ex) {
                ex.printStackTrace();
                retVal = ReturnValue.GENERAL_FAILURE;
            }
        }
        return comments;
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
        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query)) {

                pst.setString(1, name);
                pst.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        }
    }

    public ReturnValue getRetVal() {
        return retVal;
    }
}
