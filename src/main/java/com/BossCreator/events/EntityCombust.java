package com.BossCreator.events;

import com.BossCreator.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;

public class EntityCombust implements Listener {
    private Main plugin;
    public EntityCombust(){
        plugin = Main.getInstance();
    }
    @EventHandler
    public void onCombust(EntityCombustEvent e){
        for(int i=0;i<plugin.bossManager.getAmount();i++){
            plugin.bossManager.getBoss(i).onCombust(e);
        }
        for(int i=0;i<plugin.mobManager.getAmount();i++){
            plugin.mobManager.getMob(i).onCombust(e);
        }
    }
}
