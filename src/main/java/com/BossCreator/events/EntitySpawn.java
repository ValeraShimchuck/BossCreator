package com.BossCreator.events;

import com.BossCreator.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntitySpawn implements Listener {
    private Main plugin;
    public EntitySpawn(){
        plugin = Main.getInstance();
    }
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent e){
        //plugin.getLogger().info(String.valueOf(plugin.bossManager.getAmount()));
        for(int i=0;i<plugin.bossManager.getAmount();i++){
            plugin.bossManager.getBoss(i).onSpawnBoss(e);
        }
        for(int i=0;i<plugin.mobManager.getAmount();i++){
            plugin.mobManager.getMob(i).onSpawn(e);
        }
    }
}
