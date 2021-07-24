package com.BossCreator.commands;

import com.BossCreator.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportToBoss implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!commandSender.hasPermission("bosscreator.tpboss"))return true;
        if(!(commandSender instanceof Player))return true;
        if(strings.length !=1)return false;
        Location loc = Main.getInstance().bossManager.getBoss(Integer.parseInt(strings[0])).getLocation();
        Player p= (Player) commandSender;
        p.teleport(loc);
        return true;
    }
}
