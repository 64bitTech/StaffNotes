package com.staffnotes;
import com.staffnotes.Listeners.ChatListener;
import com.staffnotes.classes.NotesDatabase;
import com.staffnotes.Listeners.TabComplete;
import com.staffnotes.Listeners.CmdExecuter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class StaffNotes extends JavaPlugin {
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        getLogger().info("[StaffNotes] plugin is being enabled...");
        reloadConfig();
        NotesDatabase.setdbLocation(getDataFolder().toPath().resolve("StaffNotes.db"));
        // Register command
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            boolean success = dataFolder.mkdirs(); // Create the directory and any necessary parent directories
            if (success) {
                getLogger().info("[StaffNotes]Directory created successfully: " + dataFolder.getAbsolutePath());
            } else {
                getLogger().warning("[StaffNotes]Failed to create directory: " + dataFolder.getAbsolutePath());
            }
        } else {
            getLogger().info("[StaffNotes]Directory already exists: " + dataFolder.getAbsolutePath());
        }
        this.getCommand("notes").setExecutor(new CmdExecuter());
        this.getCommand("notes").setTabCompleter(new TabComplete());
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        try {
            NotesDatabase.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getLogger().info("[StaffNotes] plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("[StaffNotes] plugin is being disabled...");
        try {
            NotesDatabase.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getLogger().info("[StaffNotes] plugin has been disabled.");
    }


    public void reloadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
}