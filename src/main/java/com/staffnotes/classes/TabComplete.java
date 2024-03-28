package com.staffnotes.classes;

import com.staffnotes.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    private FileConfiguration config;
    public TabComplete(Main plugin){
        this.config = plugin.getConfig();
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("notes.default")) return null;
        List<String> completions = new ArrayList<>();
        List<String> noteTypes = config.getStringList("NoteTypes");

        if (args.length == 1) {
            // Tab complete player names if the first argument is empty
            String input = args[0].toLowerCase();
            if ("add".startsWith(input)) {
                completions.add("Add");
            }
            if ("remove".startsWith(input)) {
                completions.add("Remove");
            }
            if ("get".startsWith(input)) {
                completions.add("Get");
            }
            if ("help".startsWith(input)) {
                completions.add("Help");
            }
        } else if (args.length == 2) {
            String input = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                String playerName = player.getName();
                if (playerName.toLowerCase().startsWith(input)) {
                    completions.add(playerName);
                }
            }
        } else if (args.length == 3) {
            // Add configured options for the notetype parameter
            String input = args[2].toLowerCase(); // User's input
            for (String type : noteTypes) {
                if (type.toLowerCase().startsWith(input)) {
                    completions.add(type);
                }
            }
        }
        return completions;
    }
}