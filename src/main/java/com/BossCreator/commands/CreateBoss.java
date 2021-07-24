package com.BossCreator.commands;

import com.BossCreator.Bosses.ZombieBoss;
import com.BossCreator.Main;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CreateBoss implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(command.getName().equals("createboss")){
            if(!commandSender.hasPermission("bosscreator.createboss"))return true;
            if(!(commandSender instanceof Player))return true;
            if(strings.length != 1)return false;
            Player p = ((Player) commandSender).getPlayer();
            Main.getInstance().bossManager.getBoss(Integer.parseInt(strings[0])).spawnBoss(p.getLocation());
        }
        return true;
    }
}
