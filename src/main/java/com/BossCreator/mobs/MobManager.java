package com.BossCreator.mobs;

import com.BossCreator.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;

public class MobManager {
    private Main plugin;
    private HashMap<Integer,IMob> mobs=new HashMap<>();
    private Location loc =new Location(Bukkit.getWorlds().get(0),0,0,0);
    public MobManager(){
        plugin=Main.getInstance();
        init();
    }
    private void init(){
        mobs.put(0,new MinionZombie(loc));
    }
    public IMob getMob(int id){
        return mobs.get(id);
    }
    public int getAmount(){return mobs.size();}

}
