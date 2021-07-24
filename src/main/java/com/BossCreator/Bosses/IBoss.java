package com.BossCreator.Bosses;

import com.BossCreator.Bosses.utils.BossHealth;
import com.BossCreator.Bosses.utils.BossTime;
import com.BossCreator.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface IBoss {
    int getID();
    void createFile();
    //Entity getEntity();
    File getFile();
    FileConfiguration getConfig();
    Location getLocation();
    double getBossDamage();
    double getBossHealth();
    String getName();
    BossTime getTime();
    IBoss rebuild();
    HashMap<Player,Double> getSortedDamageMap();
    void createHologram(Player player);
    void deleteHologram(Player player);
    void deleteAllHolograms();
    void onDeath(EntityDeathEvent event);
    void onDamage(EntityDamageEvent event);
    void onDamageByPlayer(EntityDamageByEntityEvent event);
    void onSpawnBoss(CreatureSpawnEvent event);
    void onCombust(EntityCombustEvent event);
    void spawnBoss();
    void spawnBoss(Location location);
    boolean isSpawn();
    void deleteBoss();

    public default HashMap<Player,Double> getSortedPlayers(HashMap<Player, Double> map){
        HashMap<Player,Double> sortedMap = new HashMap<>();
        HashMap<Player, Double> unsortedMap =  map;
        while(!unsortedMap.isEmpty()){
            Player tempPlayer = null;
            for(Player p: unsortedMap.keySet()){
                if(tempPlayer ==null){
                    tempPlayer=p;
                }else{
                    if(unsortedMap.get(tempPlayer) < unsortedMap.get(p)){
                        tempPlayer=p;
                    }
                }
            }
            sortedMap.put(tempPlayer,unsortedMap.get(tempPlayer));
            unsortedMap.remove(tempPlayer);
        }
        return sortedMap;
    }
    public default String getStringBossHealth(BossHealth bh){
        double health = bh.getHealth();
        double maxHealth = bh.getMaxHealth();
        double healthConstant = 8/maxHealth;
        int redHearts = (int) (health*healthConstant);
        String text = ChatColor.RED+"";
        for(int i=0;i<redHearts;i++){
            text = text+"❤";
        }
        int whiteHearts = 8-redHearts;
        text = text+""+ChatColor.WHITE;
        for(int i=0;i<whiteHearts;i++){
            text=text+"❤";
        }
        return text;
    }
    public default void addDataToDB(){
        long time = Instant.now().getEpochSecond();
        int id=getID();
        int players=3;
        HashMap<Player,Double> map = getSortedDamageMap();
        List<Player> playerList = new ArrayList<>();
        Set<Player> playerSet = map.keySet();
        for(Player p:playerSet){
            playerList.add(p);
        }
        String top = "[";
        for(int i =0;i<players;i++){
            String name="";
            String damage="";
            String info ="";
            if(i+1>playerList.size()){
                name ="-null-";
                damage="-null-";
            }else{
                name =playerList.get(i).getName();
                damage= String.valueOf(map.get(playerList.get(i)));
            }
            if(i+1 >= players){
                info =String.format("{name= %s, damage: %s}]",name,damage);
            }else{
                info =String.format("{name= %s, damage: %s},",name,damage);
            }
            top=top+info;
        }

        try {
            Connection c = Main.getInstance().sql.getConnection();
            Statement s = c.createStatement();
            s.executeUpdate(String.format("INSERT INTO boss_data VALUES(null,%d,%d,'%s');",id,time,top));
            s.close();
            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
