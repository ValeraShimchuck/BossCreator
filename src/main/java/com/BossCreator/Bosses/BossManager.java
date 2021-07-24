package com.BossCreator.Bosses;

import com.BossCreator.Main;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.util.HashMap;

public class BossManager {
    private Main plugin=Main.getInstance();
    private HashMap<Integer, IBoss> boss=new HashMap<>();
    private WorldServer world = ((CraftWorld)plugin.getServer().getWorld("world")).getHandle();
    public BossManager(){

        init();
        Main.getScheduler().scheduleSyncRepeatingTask(plugin,getRunnable(),0,20);
    }
    private void init(){
        boss.put(0,new ZombieBoss(world).rebuild());
        boss.put(1,new IlagerBoss(world).rebuild());
    }
    public IBoss getBoss(int id){
        return boss.get(id);
    }
    public int getAmount(){return boss.size();}
    public Runnable getRunnable(){
        return new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<getAmount();i++){
                    if(getBoss(i).getTime().isSpawned()){
                        Chunk chunk = getBoss(i).getLocation().getChunk();
                        if(chunk.isLoaded()){
                            if(!getBoss(i).isSpawn()){
                                getBoss(i).spawnBoss();
                            }
                        }
                    }
                }
            }
        };
    }
}
