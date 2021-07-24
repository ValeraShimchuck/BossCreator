package com.BossCreator.events;

import com.BossCreator.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {
    private Main plugin;
    public EntityDamageByEntity(){
        plugin=Main.getInstance();
    }
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event){
        for(int i=0;i<plugin.bossManager.getAmount();i++){
            plugin.bossManager.getBoss(i).onDamageByPlayer(event);
        }
    }
}
