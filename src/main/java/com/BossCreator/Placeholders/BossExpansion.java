package com.BossCreator.Placeholders;

import com.BossCreator.Bosses.IBoss;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BossExpansion extends PlaceholderExpansion{
    @Override
    public String getIdentifier() {
        return "";
    }

    @Override
    public String getAuthor() {
        return "_The_BaJlePa_";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if(player ==null)return null;
        if(params.equals("player_name")){return player.getName();}
        if(params.equals("time")){return "{time%d}";}
        if(params.equals("boss_name")){return "{boss_name%d}";}
        return null;
    }


}
