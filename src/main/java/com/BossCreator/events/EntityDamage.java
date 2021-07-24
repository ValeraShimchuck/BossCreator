package com.BossCreator.events;

import com.BossCreator.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamage implements Listener {
    private Main plugin;
    public EntityDamage(){
        plugin = Main.getInstance();
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e){
        for(int i=0;i<plugin.bossManager.getAmount();i++){
            plugin.bossManager.getBoss(i).onDamage(e);
        }
        for(int i=0;i<plugin.mobManager.getAmount();i++){
            plugin.mobManager.getMob(i).onDamage(e);
        }
    }
}
