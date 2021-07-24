package com.BossCreator.Placeholders;

import com.BossCreator.Main;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

public class BossName implements PlaceholderReplacer {
    private int id;
    private Main plugin = Main.getInstance();
    public BossName(int id){
        this.id=id;
    }
    @Override
    public String update() {
        return plugin.bossManager.getBoss(id).getName();
    }
}
