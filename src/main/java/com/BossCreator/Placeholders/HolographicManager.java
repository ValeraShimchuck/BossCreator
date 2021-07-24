package com.BossCreator.Placeholders;

import com.BossCreator.Main;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

public class HolographicManager {
    private Main plugin = Main.getInstance();
    public void registerPlaceholders(){
        HologramsAPI.registerPlaceholder(plugin,"{test}",1D,new Test());
        for(int i=0;i<plugin.bossManager.getAmount();i++){
            HologramsAPI.registerPlaceholder(plugin,String.format("{boss_name%d}",i),0.1D,new BossName(i));
            HologramsAPI.registerPlaceholder(plugin,String.format("{time%d}",i),0.1D,new BossTimePlaceholder(i));
        }
    }
}
