package com.staffnotes.Listeners;

import com.staffnotes.classes.ActivityStore;
import com.staffnotes.commands.Commands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChat(final AsyncPlayerChatEvent e){
        // chat listener implemented to listen for /notes remove function
        // when player chats look see if activity object exists for user
        // scanity check make sure activity is for player that sent the chat message
        // sanity check that activity is remove, future activity's a possibility
        if(ActivityStore.getActivity(e.getPlayer().getUniqueId()) == null ||
                ActivityStore.getActivity(e.getPlayer().getUniqueId()).getActivity() != "remove") return;
        // execute handleChat with players chat response
        Commands.handleChat(e.getPlayer(), e.getMessage());
        // cancel chat so it deosn't get sent to server
        e.setCancelled(true);
    }
}