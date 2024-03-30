package com.staffnotes.classes;

import java.sql.*;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class NotesDatabase {
    private final JavaPlugin plugin;
    private Connection connection;


    public NotesDatabase(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        // connect to database if not already connected
        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().toPath().resolve("StaffNotes.db"));
        createTable();
    }

    public void disconnect() throws SQLException {
        // disconnect from database
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void createTable() throws SQLException {
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
    public ResultSet getNotesByUUID(String uuid) {
        String query = "SELECT * FROM notes WHERE uuid = ?";
        try  {
            this.connect();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, uuid);
            ResultSet rs = statement.executeQuery();
            return rs;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public ResultSet getNotesByUUID(String uuid, String noteType) {
        String query = "SELECT * FROM notes WHERE uuid = ? AND notetype = ?";
        try {
            this.connect();
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

    public ResultSet getNotesAll(){
        String query = "SELECT * FROM notes ORDER BY playername ASC, id ASC";
        try{
            this.connect();
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }


    public boolean addNote(String playerName, String staffName, String uuid, String noteType, String note) {
        try {
            this.connect();
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
    public boolean removeNote(int id) {
        try{
            this.connect();
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