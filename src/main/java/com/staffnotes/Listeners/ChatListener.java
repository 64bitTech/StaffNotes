package com.staffnotes.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.staffnotes.StaffNotes;

public class ChatListener implements Listener{
    private StaffNotes plugin;

    public ChatListener(StaffNotes plugin){
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChat(final AsyncPlayerChatEvent e){
        // chat listener implemented to listen for /notes remove function
        // when player chats look see if activity object exists for user
        if(plugin.getCommands().getActivity() == null) return;
        // scanity check make sure activity is for player that sent the chat message
        if(plugin.getCommands().getActivity().Player().getName() != e.getPlayer().getName()) return;
        // sanity check that activity is remove, future activity's a possibility
        if(plugin.getCommands().getActivity().Activity() != "remove") return;
        // execute handleChat with players chat response
        plugin.getCommands().handleChat(e.getPlayer(), e.getMessage());
        // cancel chat so it deosn't get sent to server
        e.setCancelled(true);
    }
}