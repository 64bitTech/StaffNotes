package com.staffnotes.commands;

import com.staffnotes.Main;
import com.staffnotes.classes.Info;
import com.staffnotes.classes.NotesDatabase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Cmds {
    private FileConfiguration config;

    private Info activity;
    private NotesDatabase notesDatabase;
    public Info getActivity(){
        return this.activity;
    }
    public Cmds(Main plugin){
        this.notesDatabase = plugin.getNotesDatabase();
        this.config = plugin.getConfig();
    }


    public boolean handleAddCommand(Player player, String[] args) {
        // Implement logic for /notes add subcommand
        String playerName = args[1];
        String noteType = "";
        List<String> noteTypes = config.getStringList("NoteTypes");
        for (String type : noteTypes){
            if (type.equalsIgnoreCase(args[2])){
                noteType = type;
                break;
            }
        }
        if (noteType == "") {
            player.sendMessage(config.getString("Error1").replace("%NoteTypes%", String.join(", ",noteTypes)));
            return false;
        }
        String[] playerInfo;
        String note = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        playerInfo = getplayerInfo(playerName);
        if (playerInfo == null) {
            player.sendMessage(config.getString("Error4").replace("%PlayerName%",playerInfo[0]));
            return true;
        }
        // Insert note into database
        try {
            notesDatabase.connect();
            boolean ret = notesDatabase.addNote(playerInfo[0],player.getName(), playerInfo[1], noteType, note);
            if (ret) {
                player.sendMessage(config.getString("SuccessAdd").replace("%PlayerName%",playerInfo[0]));
                return true;
            } else {
                player.sendMessage(config.getString("FailedAdd").replace("%PlayerName%",playerInfo[0]));
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            player.sendMessage(config.getString("Error3"));
            return false;
        }
    }
    public boolean handleGetCommand(Player player) {
        // Implement logic for /notes get all subcommand
        // Example: /notes get all
        boolean hasData =false;
        try {
            player.sendMessage(config.getString("GetAll"));
            ResultSet rs;
            notesDatabase.connect();
            rs = notesDatabase.getNotesAll();
            SimpleDateFormat sdf = new SimpleDateFormat(config.getString("DateTimeFormat"));
            if (rs != null) {
                while (rs.next()) {
                    String playerName = rs.getString("playername");
                    Date date = rs.getDate("created_at");
                    String dbnoteType = rs.getString("notetype");
                    String note = rs.getString("note");
                    String staffName = rs.getString("staffname");
                    String formattedDateTime = date.toString(); // .toLocalDateTime().format(DateTimeFormatter.ofPattern(config.getString("DateTimeFormat")));
                    Date now = new Date();
                    Integer DaysSince = Math.round( (now.getTime() - date.getTime()) / 86400000 );
                    player.sendMessage(config.getString("ListOutput").replace("%PlayerName%",playerName).replace("%NoteType%",dbnoteType).replace("%Note%",note).replace("%StaffName%",staffName).replace("%CreatedAt%",formattedDateTime).replace("%DaysSince%",String.valueOf(DaysSince)));
                    hasData = true;
                }
            }
            if(!hasData) player.sendMessage(config.getString("Error2").replace("%PlayerName%",""));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage(config.getString("Error3"));
            return false;
        }
    }
    public boolean handleGetCommand(Player player, String[] args) {
        // Implement logic for /notes get subcommand
        // Example: /notes get <playername>
        //args[1] stores entered PlayerName
        //args[2] stores NoteType

        String[] playerInfo;
        playerInfo = getplayerInfo(args[1]);
        String noteType = "";
        List<String> noteTypes = config.getStringList("NoteTypes");
        boolean hasData =false;
        if (playerInfo!=null) {
            try {
                //player.sendMessage("UUID of " + playerInfo[0] + ": " + playerInfo[1]);
                ResultSet rs;
                notesDatabase.connect();
                if (args.length == 3) {
                    rs = notesDatabase.getNotesByUUID(playerInfo[1].toString(),args[2]);
                    for (String type : noteTypes){
                        if (type.equalsIgnoreCase(args[2])){
                            noteType = type;
                            break;
                        }
                    }
                    if (noteType == "") {
                        player.sendMessage(config.getString("Error1").replace("%NoteTypes%", String.join(", ",noteTypes)));
                        return false;
                    }
                } else {
                    rs = notesDatabase.getNotesByUUID(playerInfo[1].toString());
                }
                if (rs != null) {
                    player.sendMessage(config.getString("ListHeader").replace("%PlayerName%",playerInfo[0]));
                    while (rs.next()) {
                        String playerName = rs.getString("playername");
                        Date date = rs.getDate("created_at");
                        String dbnoteType = rs.getString("notetype");
                        String note = rs.getString("note");
                        String staffName = rs.getString("staffname");
                        String formattedDateTime = date.toString();
                        Date now = new Date();
                        Integer DaysSince = Math.round( (now.getTime() - date.getTime()) / 86400000 );
                        player.sendMessage(config.getString("ListOutput").replace("%PlayerName%",playerName).replace("%NoteType%",dbnoteType).replace("%Note%",note).replace("%StaffName%",staffName).replace("%CreatedAt%",formattedDateTime).replace("%DaysSince%",String.valueOf(DaysSince)));
                        hasData = true;
                    }
                }
                if(!hasData) player.sendMessage(config.getString("Error2").replace("%PlayerName%",playerInfo[0]));
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(config.getString("Error3"));
                return false;
            }
        } else {
            player.sendMessage(config.getString("Error4").replace("%PlayerName%",args[1]));
            return true;
        }
    }

    public boolean handleRemoveCommand(Player player, String[] args) {
        // Implement logic for /notes remove subcommand
        // Example: /notes remove <playername>
        // args[1] contains target PlayerName
        // args[2] contains NoteType
        if (args.length == 0){
            config.getString("ErrorPlayerReq");
            return true;
        }
        String[] playerInfo;
        List<Integer> dataKeys = new ArrayList<Integer>();
        playerInfo = getplayerInfo(args[1]);
        String noteType = "";
        List<String> noteTypes = config.getStringList("NoteTypes");
        if (args.length == 3) {
            for (String type : noteTypes){
                if (type.equalsIgnoreCase(args[2])){
                    noteType = type;
                    break;
                }
            }
            if (noteType == "") {
                player.sendMessage(config.getString("Error1").replace("%NoteTypes%", String.join(", ",noteTypes)));
                return false;
            }
        }
        boolean hasData = false;
        if (playerInfo!=null) {
            try {
                ResultSet rs;
                notesDatabase.connect();
                if (args.length == 3) {
                    rs = notesDatabase.getNotesByUUID(playerInfo[1].toString(),args[2]);
                } else {
                    rs = notesDatabase.getNotesByUUID(playerInfo[1].toString());
                }
                if (rs == null) return false;
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String playerName = rs.getString("playername");
                    Date date = rs.getDate("created_at");
                    String dbnoteType = rs.getString("notetype");
                    String note = rs.getString("note");
                    String staffName = rs.getString("staffname");
                    String formattedDateTime = date.toString();
                    Date now = new Date();
                    Integer DaysSince = Math.round( (now.getTime() - date.getTime()) / 86400000 );
                    player.sendMessage(config.getString("ListOutput").replace("%PlayerName%",playerName).replace("%NoteType%",dbnoteType).replace("%Note%",note).replace("%StaffName%",staffName).replace("%CreatedAt%",formattedDateTime).replace("%DaysSince%",String.valueOf(DaysSince)));
                    hasData = true;
                }
                if (hasData) {
                    activity = new Info(player, "remove", dataKeys);
                    player.sendMessage(config.getString("Info"));
                    player.sendMessage(config.getString("Info2"));
                } else {
                    player.sendMessage(config.getString("Error2").replace("%PlayerName%",playerInfo[1]));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                player.sendMessage(config.getString("Error3"));
                return false;
            }
        } else {
            player.sendMessage(config.getString("Error4").replace("%PlayerName%",args[1]));
            return true;
        }
        return true;
    }

    public void handleChat(Player player,String noteID){
        if (noteID.toLowerCase().contains("cancel")) {
            player.sendMessage(config.getString("Canceled"));
            activity = null;
            return;
        }
        int i;
        try {
            i = Integer.parseInt(noteID);
        } catch (NumberFormatException e) {
            player.sendMessage(config.getString("Canceled2"));
            activity = null;
            return;
        }
        List<Integer> rs = activity.dataKeys();
        if (rs!=null) {
            for (int x : rs){
                if (i == x) {
                    if (removeFromDBbyID(i)) {
                        player.sendMessage(config.getString("SuccessRemove"));
                        activity = null;
                        return;
                    } else {
                        player.sendMessage(config.getString("FailedRemove"));
                        activity = null;
                        return;
                    }
                }
            }
            player.sendMessage(config.getString("FailedRemove2"));
        }
    }
    private  boolean removeFromDBbyID(int id){
        try {
            notesDatabase.connect();
            return notesDatabase.removeNote(id);
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    private String[] getplayerInfo(String playerName) {
        String[] ret = new String[2];
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            ret[0] = player.getName();
            ret[1] = player.getUniqueId().toString().replace("-","");
            return ret;
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            ret[0] = offlinePlayer.getName();
            if (ret[0] == null){
                ret[0] = playerName;
            }
            ret[1] = offlinePlayer.getUniqueId().toString().replace("-","");
            return ret;
        }
    }

}
