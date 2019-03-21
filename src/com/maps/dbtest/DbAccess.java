package com.maps.dbtest;

import com.mysql.jdbc.Connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class DbAccess {
    private boolean tryReconnect;
    private Connection connection;
    private AtomicBoolean busy;
    String connectionURL;
    public AtomicBoolean getBusy() {
        return busy;
    }

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
    public ArrayList<Event> getEvents(String city){

        ArrayList<Event> list = new ArrayList<Event>();
        String query = "SELECT event.idEvent, event.user_iduser, event.city, user.name, event.date, event.description\n" +
                "FROM event\n" +
                "INNER JOIN user ON event.user_iduser = user.iduser where city = ?;";
        boolean  retVal = false;

        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query)) {

                pst.setString(1, city);
                ResultSet rs = pst.executeQuery();

                while (rs.next()) {
                    list.add(new Event(
                                rs.getInt(1),
                                rs.getInt(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getString(5),
                                rs.getString(6)));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        }
        return list;
    }

    /**
     * Adds event to database
     * @param event
     */
    public void addEvent(Event event){
        String query = "INSERT INTO `happening`.`event` (`user_iduser`, `city`, `date`, `description`) VALUES (?, ?, ?, ?);";
        boolean  retVal = false;
        if (checkConnection()) {
            try (PreparedStatement pst = connection.prepareStatement(query)) {

                pst.setInt(1, event.getUserId());
                pst.setString(2, event.getCity());
                pst.setString(3, event.getDate());
                pst.setString(4, event.getDescription());
                pst.execute();

            } catch (SQLException ex) {
                ex.printStackTrace();

            }
        }
    }

}
