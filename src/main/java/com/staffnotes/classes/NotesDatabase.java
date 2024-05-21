package com.staffnotes.classes;

import java.nio.file.Path;
import java.sql.*;

public class NotesDatabase {

    private static Connection connection;
    private static Path dbLocation;
    public static void setdbLocation(Path path){
        dbLocation = path;
    }
    public static void connect() throws SQLException {
        // connect to database if not already connected
        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbLocation);
        createTable();
    }

    public static void disconnect() throws SQLException {
        // disconnect from database
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private static void createTable() throws SQLException {
        // create blank table if database was just created
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    CREATE TABLE IF NOT EXISTS notes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    playername TEXT NOT NULL,
                    staffname TEXT NOT NULL,
                    uuid TEXT NOT NULL,
                    notetype TEXT NOT NULL,
                    note TEXT NOT NULL)
                    """
        )) {
            statement.executeUpdate();
        }
    }
    public static ResultSet getNotesByUUID(String uuid) {
        String query = "SELECT * FROM notes WHERE uuid = ?";
        try  {
            connect();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, uuid);
            ResultSet rs = statement.executeQuery();
            return rs;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public static ResultSet getNotesByUUID(String uuid, String noteType) {
        String query = "SELECT * FROM notes WHERE uuid = ? AND notetype = ?";
        try {
            connect();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1,  uuid);
            statement.setString(2, noteType);
            ResultSet rs = statement.executeQuery();
            return rs;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet getNotesAll(){
        String query = "SELECT * FROM notes ORDER BY playername ASC, id ASC";
        try{
            connect();
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }


    public static boolean addNote(String playerName, String staffName, String uuid, String noteType, String note) {
        try {
            connect();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO notes (playername,staffName, uuid, notetype, note) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, playerName);
            statement.setString(2, staffName);
            statement.setString(3, uuid.toString());
            statement.setString(4, noteType);
            statement.setString(5, note);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
    public static boolean removeNote(int id) {
        try{
            connect();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM notes WHERE id = ?");
            statement.setInt(1, id);
            int rows = statement.executeUpdate();
            if (rows == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

}