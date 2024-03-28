package com.staffnotes.Listeners;

import com.staffnotes.commands.Cmds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.staffnotes.Main;
public class CmdExecuter implements CommandExecutor {
    private Main plugin;
    private Cmds cmds;
    public CmdExecuter(Main plugin) {
        this.cmds = plugin.getCommands();
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
                        return cmds.handleAddCommand((Player) sender, args);
                    }
                case "get":
                    if (!sender.hasPermission("notes.view")) {
                        sender.sendMessage("§cYou do not have permissions to run this command");
                        return true;
                    } else {
                        if (args.length >= 2){
                            if (args[1].equalsIgnoreCase("all")) {
                                if (!sender.hasPermission("notes.view.all")) {
                                    sender.sendMessage("§cYou do not have permissions to run this command");
                                    return true;
                                } else {
                                    return cmds.handleGetCommand((Player) sender);
                                }
                            }else {
                                return cmds.handleGetCommand((Player) sender,args);
                            }
                        } else {
                            if (!sender.hasPermission("notes.view.all")) {
                                sender.sendMessage("§cPlease enter a PlayerName");
                                return true;
                            } else {
                                return cmds.handleGetCommand((Player) sender);
                            }
                        }
                    }
                case "remove":
                    if (!sender.hasPermission("notes.remove")) {
                        sender.sendMessage("§cYou do not have permissions to run this command");
                        return true;
                    } else {
                        return cmds.handleRemoveCommand((Player) sender, args);
                    }
                default:
                    return false;
            }
        }
        return false;
    }




}
