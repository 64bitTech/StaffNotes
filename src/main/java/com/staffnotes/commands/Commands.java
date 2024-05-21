package com.staffnotes.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.staffnotes.classes.ActivityClass;
import com.staffnotes.classes.ActivityStore;
import com.staffnotes.classes.NotesDatabase;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;


import static com.staffnotes.StaffNotes.config;

public class Commands {

    public static boolean handleAddCommand(Player player, String[] args, boolean neverPlayed) {
        // Implement logic for /notes add subcommand
        String playerName = args[1];
        String noteType = "";
        List<String> noteTypes = config.getStringList("NoteTypes");
        // Require a noteType to be entered
        if (args.length == 2){
            player.sendMessage(config.getString("NoteTypeRequired").replace("%NoteTypes%",String.join(", ",noteTypes)));
            return true;
        }
        // Parse availilbe noteTypes to confirm entered noteType is valid
        for (String type : noteTypes){
            if (type.equalsIgnoreCase(args[2])){
                noteType = type;
                break;
            }
        }
        if (noteType == "") {
            //notify player that noteType entered is not valid
            player.sendMessage(config.getString("Error1").replace("%NoteTypes%", String.join(", ",noteTypes)));
            return false;
        }
        String[] playerInfo;
        playerInfo = getplayerInfo(playerName, neverPlayed);
        if (playerInfo == null) {
            player.sendMessage(config.getString("Error4").replace("%PlayerName%",playerInfo[0]));
            return true;
        }
        // combine all "args" after Note Type to make single string note
        String note = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

        // Insert note into database
        boolean ret = NotesDatabase.addNote(playerInfo[0],player.getName(), playerInfo[1], noteType, note);
        if (ret) {
            player.sendMessage(config.getString("SuccessAdd").replace("%PlayerName%",playerInfo[0]));
            return true;
        } else {
            player.sendMessage(config.getString("FailedAdd").replace("%PlayerName%",playerInfo[0]));
            return false;
        }
    }
    public static boolean handleGetCommand(Player player) {
        // overload handleGetCommand for 0 args and /Notes Get All
        return handleGetCommand(player,null, true);
    }
    public static boolean handleGetCommand(Player player, String[] args, boolean neverPlayed) {
        // Implement logic for /notes get subcommand
        // Example: /notes get <playername>
        //args[1] stores entered PlayerName
        //args[2] stores NoteType
        //args null = get all notes

        ResultSet rs = null;
        boolean hasData = false;
        String[] playerInfo = {"All Players","none"};

        if (args != null) {
            playerInfo = getplayerInfo(args[1],neverPlayed);
            String noteType = "";
            List<String> noteTypes = config.getStringList("NoteTypes");
            if (playerInfo != null) {
                if (args.length == 3) {
                    // if command includes NoteType check that it's in the list of available types
                    for (String type : noteTypes) {
                        if (type.equalsIgnoreCase(args[2])) {
                            noteType = type;
                            break;
                        }
                    }
                    if (noteType == "") {
                        player.sendMessage(config.getString("Error1").replace("%NoteTypes%", String.join(", ", noteTypes)));
                        return true;
                    }
                    // get notes by UUID and NoteType
                    rs = NotesDatabase.getNotesByUUID(playerInfo[1].toString(), args[2]);
                } else {
                    // get notes by UUID only
                    rs = NotesDatabase.getNotesByUUID(playerInfo[1].toString());
                }
            } else {
                player.sendMessage(config.getString("Error5").replace("%PlayerName%",args[1]));
                return true;
            }
        } else {
            rs = NotesDatabase.getNotesAll();
        }
        try {
            if (rs != null) {
                // if results found parse though and send to player
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
            // if no notes found notify user
            if(!hasData) player.sendMessage(config.getString("Error2").replace("%PlayerName%",playerInfo[0]));
            return true;
        } catch (SQLException e) {
            // handle SQLException since we are dealing directly with ResultSet
            e.printStackTrace();
            player.sendMessage(config.getString("Error3"));
            return false;
        }
    }

    public static boolean handleRemoveCommand(Player player, String[] args, boolean neverPlayed) {
        // Implement logic for /notes remove subcommand
        // Example: /notes remove <playername>
        // args[1] contains target PlayerName
        // args[2] contains NoteType

        // handle if /notes remove is entered without a player name, let player know playername is required
        if (args.length == 1){
            player.sendMessage(config.getString("ErrorPlayerReq"));
            return true;
        }
        String[] playerInfo = getplayerInfo(args[1], neverPlayed); // get name and UUID of entered player name
        List<Integer> dataKeys = new ArrayList<Integer>();
        List<String> noteTypes = config.getStringList("NoteTypes");
        String noteType = "";

        // if noteType argument is part of the command, loop though available noteTypes to make sure it's valid
        if (args.length == 3) {
            for (String type : noteTypes){
                if (type.equalsIgnoreCase(args[2])){
                    noteType = type;
                    break;
                }
            }
            // if entered noteType not found notify player that it is invalid and give available options
            if (noteType == "") {
                player.sendMessage(config.getString("Error1").replace("%NoteTypes%", String.join(", ",noteTypes)));
                return false;
            }
        }
        boolean hasData = false;

        // if entered playername existed do stuff
        if (playerInfo!=null) {
            try {
                ResultSet rs;
                // query database by UUID or UUID and NoteType
                if (args.length == 3) {
                    rs = NotesDatabase.getNotesByUUID(playerInfo[1].toString(),args[2]);
                } else {
                    rs = NotesDatabase.getNotesByUUID(playerInfo[1].toString());
                }
                if (rs == null) return false;
                // if notes exists parse though them and send to player so they can choose what note to remove
                player.sendMessage(config.getString("ListHeader").replace("%PlayerName%",playerInfo[0]));
                while (rs.next()) {
                    //parse though notes and send to user
                    int id = rs.getInt("id");
                    dataKeys.add(id); // used later to confirm that user can only remove notes presented to them
                    String playerName = rs.getString("playername");
                    Date date = rs.getDate("created_at");
                    String dbnoteType = rs.getString("notetype");
                    String note = rs.getString("note");
                    String staffName = rs.getString("staffname");
                    String formattedDateTime = date.toString();
                    Date now = new Date();
                    Integer DaysSince = Math.round( (now.getTime() - date.getTime()) / 86400000 );
                    player.sendMessage(config.getString("ListOutput_Remove").replace("%NoteID%", String.valueOf(id)).replace("%PlayerName%",playerName).replace("%NoteType%",dbnoteType).replace("%Note%",note).replace("%StaffName%",staffName).replace("%CreatedAt%",formattedDateTime).replace("%DaysSince%",String.valueOf(DaysSince)));
                    hasData = true;
                }
                if (hasData) {
                    // if notes were found set Activity monitored by chatListener so it will now listen for the player to type a note ID
                    ActivityStore.setActivity(player.getUniqueId(),new ActivityClass("remove",dataKeys));
                    player.sendMessage(config.getString("Info"));
                    player.sendMessage(config.getString("Info2"));
                    return true;
                } else {
                    // let the player know that no notes were found.
                    player.sendMessage(config.getString("Error2").replace("%PlayerName%",playerInfo[1]));
                    return true;
                }
            } catch (SQLException e) {
                // handle SQLExeception here since we're dealing directly with a ResultSet
                e.printStackTrace();
                player.sendMessage(config.getString("Error3"));
                return false;
            }
        } else {
            // let player know that the entered player name was not found
            player.sendMessage(config.getString("Error4").replace("%PlayerName%",args[1]));
            return true;
        }
    }

    public static void handleChat(Player player,String noteID) {
        // executed by ChatListener if activity is remove
        // listens for player to type ID of the note they want removed
        if (noteID.toLowerCase().contains("cancel")) {
            player.sendMessage(config.getString("Canceled"));
            ActivityStore.removeActivity(player.getUniqueId());
            return;
        }

        // Parse input to confirm player has entered only a number
        int i;
        try {
            i = Integer.parseInt(noteID);
        } catch (NumberFormatException e) {
            // fail and cancel if anything else has been entered
            player.sendMessage(config.getString("Canceled2"));
            ActivityStore.removeActivity(player.getUniqueId());
            return;
        }
        // parse though list of possible ID's to prevent player from removing a note from another row by accident
        // would have used ResultSet here but Sqlite doesn't allow row seek
        List<Integer> rs = ActivityStore.getActivity(player.getUniqueId()).getDataKeys();
        if (rs != null) {
            for (int x : rs) {
                if (i == x) {
                    // if players entered value is found in the list of available values remove the row
                    if (NotesDatabase.removeNote(i)) {
                        player.sendMessage(config.getString("SuccessRemove"));
                        ActivityStore.removeActivity(player.getUniqueId());
                        return;
                    } else {
                        // notify player if remove fails from database and resets the activity
                        player.sendMessage(config.getString("FailedRemove"));
                        ActivityStore.removeActivity(player.getUniqueId());
                        return;
                    }
                }
            }
            // notifiy player that the row number they've entered does not exist in the rows they were presented.
            player.sendMessage(config.getString("FailedRemove2"));
        }
    }
    private static String[] getplayerInfo(String playerName,boolean neverplayed) {
        // Gets info for specific player name from Buikkit OfflinePlayer API
        // Playername can return Null but UUID should always be available
        // returns string array where 0 = Name returned by API or playername input if null
        // 1 = UUID
        String[] ret = new String[2];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer != null) {
            if (!offlinePlayer.hasPlayedBefore() && !neverplayed) return null;
            if (playerName.startsWith(config.getString("BedrockPrefix"))) return getplayerInfoBedrock(playerName);
            ret[0] = offlinePlayer.getName();
            if (ret[0] == null) {
                ret[0] = playerName;
            }
            ret[1] = offlinePlayer.getUniqueId().toString().replace("-", "");
        }
        return ret;
    }
    private static String[] getplayerInfoBedrock(String playerName){
        // Used to get BedrockPlayer UUID if player has not been seen on the server before
        // Impliments Geyser WebAPI v2
        // returns string array where 0 = Name returned by API
        // 1 = UUID
        String[] ret = new String[2];
        try {
            String url = String.format("https://api.geysermc.org/v2/utils/uuid/bedrock_or_java/%1$s?prefix=%2$s", playerName, config.getString("BedrockPrefix"));
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(new URL(url).openStream())).getAsJsonObject();
            ret[0] = jsonObject.get("name").getAsString();
            ret[1] = jsonObject.get("id").getAsString();
            return ret;
        }catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
