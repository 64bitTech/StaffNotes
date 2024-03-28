package com.staffnotes.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.staffnotes.Main;

public class ChatListener implements Listener{
    private Main plugin;

    public ChatListener(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChat(final AsyncPlayerChatEvent e){
        if(plugin.getCommands().getActivity() == null) return;
        if(plugin.getCommands().getActivity().Player().getName() != e.getPlayer().getName()) return;
        plugin.getCommands().handleChat(e.getPlayer(), e.getMessage());
        e.setCancelled(true);
    }
}