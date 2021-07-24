package com.BossCreator.Placeholders;

import com.BossCreator.Main;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

public class BossTimePlaceholder implements PlaceholderReplacer {
    private int id;
    private Main plugin = Main.getInstance();
    public BossTimePlaceholder(int id){
        this.id=id;
    }
    @Override
    public String update() {
        int hour = plugin.bossManager.getBoss(id).getTime().getCurrentHours();
        int min = plugin.bossManager.getBoss(id).getTime().getCurrentMinutes();
        int sec = plugin.bossManager.getBoss(id).getTime().getCurrentSeconds();
        String stringTime = "";
        if(hour > 0){
            stringTime = stringTime+hour+"Ч. ";
            stringTime = stringTime+min+"М. ";
            stringTime = stringTime+sec+"С. ";
        }else
        if(min > 0){
            stringTime = stringTime+min+"М. ";
            stringTime = stringTime+sec+"С. ";
        }else{
            stringTime = stringTime+sec+"С. ";
        }
        return stringTime;
    }
}
