package com.staffnotes.classes;

import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.List;

public class Info {
    private String activity;
    private Player player;
    private List<Integer> dataKeys;

    public Player Player() {return player;}
    public String Activity() {return activity;}
    public List<Integer> dataKeys(){return dataKeys;}
    public Info(Player player,String Activity, List<Integer> dataKeys){
        this.activity = Activity;
        this.dataKeys = dataKeys;
        this.player = player;
    }


}
