package com.BossCreator.events;

import com.BossCreator.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {
    private Main plugin;
    public EntityDeath(){
        plugin = Main.getInstance();
    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        for(int i=0;i<plugin.bossManager.getAmount();i++){
            plugin.bossManager.getBoss(i).onDeath(e);
        }
        for(int i=0;i<plugin.mobManager.getAmount();i++){
            plugin.mobManager.getMob(i).onDeath(e);
        }
    }
}
