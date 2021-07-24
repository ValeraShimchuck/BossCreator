package com.BossCreator;

import com.BossCreator.Bosses.BossManager;
import com.BossCreator.Placeholders.BossExpansion;
import com.BossCreator.Placeholders.HolographicManager;
import com.BossCreator.commands.CreateBoss;
import com.BossCreator.commands.DeleteBoss;
import com.BossCreator.commands.SpawnBoss;
import com.BossCreator.commands.TeleportToBoss;
import com.BossCreator.events.*;
import com.BossCreator.mobs.MobManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.configuration.PlaceholderAPIConfig;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

public class Main extends JavaPlugin {
    public SQLDatabase sql;
    public BossManager bossManager;
    public MobManager mobManager;
    public HolographicManager hlManager;
    public BossExpansion placeholder;
    private static Main instance;
    @Override
    public void onEnable() {
        instance = this;
        hlManager = new HolographicManager();

        File theDir = new File(getDataFolder()+File.separator+"bosses");
        if(!theDir.exists()){
            theDir.mkdirs();
        }
        try {
            sql = new SQLDatabase();
            Connection c = sql.getConnection();
            Statement s = c.createStatement();
            s.executeUpdate("CREATE TABLE IF NOT EXISTS boss_data (Id INTEGER PRIMARY KEY AUTOINCREMENT,bossID INTEGER UNSIGNED, BIGINT UNSIGNED kill_time,TEXT top_players);");
            s.close();
            c.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        File config = new File(getDataFolder()+ File.separator+"config.yml");
        if(!config.exists()){
            getLogger().info("Creating cfg file...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        getDataFolder().mkdirs();
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new EntitySpawn(),this);
        pm.registerEvents(new EntityDeath(),this);
        pm.registerEvents(new EntityCombust(),this);
        pm.registerEvents(new EntityDamage(),this);
        pm.registerEvents(new EntityDamageByEntity(),this);
        pm.registerEvents(new PlayerQuitJoin(this),this);

        getCommand("createboss").setExecutor(new CreateBoss());
        getCommand("spawnboss").setExecutor(new SpawnBoss());
        getCommand("tpboss").setExecutor(new TeleportToBoss());
        getCommand("deleteboss").setExecutor(new DeleteBoss());

        bossManager = new BossManager();
        mobManager = new MobManager();
        hlManager.registerPlaceholders();
        new BossExpansion().register();
    }

    @Override
    public void onDisable() {
        for(int i=0; i< bossManager.getAmount();i++){
            bossManager.getBoss(i).deleteBoss();
        }
    }

    public static BukkitScheduler getScheduler(){return Bukkit.getScheduler();}
    public static Main getInstance(){return instance;}

}
