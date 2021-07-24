package com.BossCreator.commands;

import com.BossCreator.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteBoss implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!commandSender.hasPermission("bosscreator.deleteboss"))return true;
        if(strings.length !=1)return false;
        Main.getInstance().bossManager.getBoss(Integer.parseInt(strings[0])).deleteBoss();
        return true;
    }
}
