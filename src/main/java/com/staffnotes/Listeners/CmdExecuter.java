package com.staffnotes.Listeners;

import com.staffnotes.StaffNotes;
import com.staffnotes.commands.Commands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdExecuter implements CommandExecutor {
    private Commands commands;
    public CmdExecuter(StaffNotes plugin) {
        this.commands = plugin.getCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("notes")) {
            if(!sender.hasPermission("notes.default")) return true;
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                return false;
            }
            switch (args[0].toLowerCase()) {
                case "add":
                    if (!sender.hasPermission("notes.add")) {
                        sender.sendMessage("§cYou do not have permissions to run this command");
                        return true;
                    } else {
                        return commands.handleAddCommand((Player) sender, args);
                    }
                case "get":
                    if (!sender.hasPermission("notes.view")) {
                        sender.sendMessage("§cYou do not have permissions to run this command");
                        return true;
                    } else {
                        if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("all"))){
                            if (!sender.hasPermission("notes.view.all")) {
                                sender.sendMessage("§cPlease enter a PlayerName");
                                return true;
                            } else {
                                return commands.handleGetCommand((Player) sender);
                            }
                        } else if (args.length >= 2){
                            return commands.handleGetCommand((Player) sender,args);
                        } else {
                            return false;
                        }
                    }
                case "remove":
                    if (!sender.hasPermission("notes.remove")) {
                        sender.sendMessage("§cYou do not have permissions to run this command");
                        return true;
                    } else {
                        return commands.handleRemoveCommand((Player) sender, args);
                    }
                default:
                    return false;
            }
        }
        return false;
    }
}
