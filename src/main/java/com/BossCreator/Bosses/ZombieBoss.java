package com.BossCreator.Bosses;

import com.BossCreator.Bosses.utils.BossHealth;
import com.BossCreator.Bosses.utils.BossTime;
import com.BossCreator.Main;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class ZombieBoss extends EntityZombie implements IBoss  {
    private File datafile;
    private FileConfiguration data;
    private Main plugin;
    private Location loc;
    private String name;
    private Double damage;
    private static Double Maxhealth;
    private BossTime time;
    private HashMap<Armor, ItemStack> armor = new HashMap<>();
    private HashMap<Skills,Integer> cooldown = new HashMap<>();
    private HashMap<Skills,Integer> skilltime = new HashMap<>();
    private HashMap<Player, Hologram> holograms = new HashMap<>();
    private HashMap<Player,Double> playerBossDamage = new HashMap<>();
    private Zombie mobEntity=null;
    private ZombieBoss boss=null;
    private WorldServer worldServer;
    private boolean isCombust;
    private boolean isLootDrop;
    private boolean isXpDrop;
    private boolean isAlive = false;
    private BossHealth bossHealth;
    public ZombieBoss(WorldServer world) {
        super(EntityTypes.ZOMBIE,world);
        worldServer = world;
        plugin=Main.getInstance();
        datafile = new File(plugin.getDataFolder()+File.separator+"bosses"+File.separator+String.format("boss%d.yml",getID()));
        createFile();
        data = YamlConfiguration.loadConfiguration(datafile);
        initVariable();
        initArmor();
        this.setPosition(loc.getX(),loc.getY(),loc.getZ());
        this.setCustomName(new ChatComponentText(name));
        this.setCustomNameVisible(true);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(damage);
        LivingEntity entity = (LivingEntity) this.getBukkitEntity();
        entity.setRemoveWhenFarAway(false);
        this.setCanPickupLoot(false);

    }
    private void initVariable(){
        double x = data.getDouble("location.x");
        double y = data.getDouble("location.y");
        double z = data.getDouble("location.z");
        World world = Bukkit.getWorld(data.getString("location.world"));

        loc = new Location(world,x,y,z);
        name = data.getString("name");
        name = name.replace("&","\u00a7");
        damage = data.getDouble("params.damage");
        Maxhealth = data.getDouble("params.Maxhealth");
        isCombust = data.getBoolean("params.combust");
        isLootDrop = data.getBoolean("params.lootdrop");
        isXpDrop = data.getBoolean("params.xpdrop");
        int hours = data.getInt("time.hours");
        int minutes = data.getInt("time.minutes");
        int seconds = data.getInt("time.seconds");
        time = new BossTime(hours,minutes,seconds,this);
        cooldown.put(Skills.SPAWN_ZOMBIES,data.getInt("skills.spawn_zombies.cooldown"));
        cooldown.put(Skills.EQUIP_ARMOR,data.getInt("skills.equip_armor.cooldown"));
        skilltime.put(Skills.EQUIP_ARMOR,data.getInt("skills.equip_armor.skill_time"));




    }
    private void initArmor(){
        for(Armor arm:Armor.values()){
            String armorCategory = arm.toString().toLowerCase();
            Material armorType = Material.valueOf(data.getString(String.format("items.%s.item",armorCategory)).toUpperCase());
            ItemStack armorItem = new ItemStack(armorType);

            Enchantment armorEnchantment = Enchantment.getByKey(NamespacedKey.minecraft(data.getString(String.format("items.%s.enchant",armorCategory))));
            int enchantmentLevel = data.getInt(String.format("items.%s.enchantlevel",armorCategory));
            ItemMeta meta = armorItem.getItemMeta();
            meta.setUnbreakable(data.getBoolean(String.format("items.%s.breakable",armorCategory)));
            meta.addEnchant(armorEnchantment,enchantmentLevel,true);
            armorItem.setItemMeta(meta);
            armor.put(arm,armorItem);
        }
    }
    @Override
    public int getID() {
        return 0;
    }

    @Override
    public void createFile() {
        if(!datafile.exists()){
            try {
                datafile.createNewFile();
                data = YamlConfiguration.loadConfiguration(datafile);
                data.set("name","Призыватель");
                data.set("location.x",0);
                data.set("location.y",0);
                data.set("location.z",0);
                data.set("location.world","world");
                data.createSection("params");
                data.set("params.damage",1);
                data.set("params.Maxhealth",20);
                data.set("params.combust",false);
                data.set("params.lootdrop",false);
                data.set("params.xpdrop",false);
                data.createSection("time");
                data.set("time.hours",0);
                data.set("time.minutes",1);
                data.set("time.seconds",0);

                createPersonalConfig();
                data.save(datafile);

            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }
    private void createPersonalConfig(){
        HashMap<Armor,Material> leatherArmor = new HashMap<>();
        Material[] materials = {Material.LEATHER_HELMET,Material.LEATHER_CHESTPLATE,Material.LEATHER_LEGGINGS,Material.LEATHER_BOOTS,Material.STONE_SWORD};
        for(int i=0; i<5;i++)leatherArmor.put(Armor.values()[i],materials[i]);
        for(Armor arm:Armor.values()){
            String armorCategory = arm.toString().toLowerCase();
            data.set(String.format("items.%s.item",armorCategory),leatherArmor.get(arm).getKey().getKey());
            data.set(String.format("items.%s.enchantlevel",armorCategory),2);
            data.set(String.format("items.%s.breakable",armorCategory),false);
            if(arm == Armor.SWORD){
                data.set(String.format("items.%s.enchant",armorCategory),"sharpness");
            }else{
                data.set(String.format("items.%s.enchant",armorCategory),"protection");
            }
        }
        data.set("params.minions_combust",false);
        data.set("params.minions_health",20);
        data.set("params.minions_damage",1);
        data.set("params.minions_lootdrop",false);
        data.set("params.minions_xpdrop",false);
        data.set("skills.spawn_zombies.cooldown",60);
        data.set("skills.equip_armor.cooldown",30);
        data.set("skills.equip_armor.skill_time",30);
    }


    @Override
    public File getFile() {
        return datafile;
    }

    @Override
    public FileConfiguration getConfig() {
        return data;
    }

    @Override
    public Location getLocation() {
        return loc;
    }

    @Override
    public double getBossDamage() {
        return damage;
    }

    @Override
    public double getBossHealth() {
        return bossHealth.getHealth();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BossTime getTime() {
        return time;
    }

    @Override
    public IBoss rebuild() {
        WorldServer world =  ((CraftWorld)loc.getWorld()).getHandle();
        return new ZombieBoss(world);
    }

    @Override
    public HashMap<Player, Double> getSortedDamageMap() {
        return getSortedPlayers(playerBossDamage);
    }

    @Override
    public void createHologram(Player player) {
        if(!isAlive){
            Location offsetLoc = loc.clone();
            double x =plugin.getConfig().getDouble("timedisplay.offset.x");
            double y =plugin.getConfig().getDouble("timedisplay.offset.y");
            double z =plugin.getConfig().getDouble("timedisplay.offset.z");
            offsetLoc.setX(loc.getX()+x);
            offsetLoc.setY(loc.getY()+y);
            offsetLoc.setZ(loc.getZ()+z);
            Hologram hologram = HologramsAPI.createHologram(plugin,offsetLoc);
            int lines = plugin.getConfig().getConfigurationSection("timedisplay.messages").getKeys(false).size();

            for(int i=0;i<lines;i++){
                String line = PlaceholderAPI.setPlaceholders(player,plugin.getConfig().getString(String.format("timedisplay.messages.line%d",i)));
                line = line.replace("%d",String.valueOf(getID()));
                line = line.replace("&","\u00a7");
                hologram.appendTextLine(line);
            }
            hologram.setAllowPlaceholders(true);
            VisibilityManager manager = hologram.getVisibilityManager();
            manager.showTo(player);
            manager.setVisibleByDefault(false);
            holograms.put(player,hologram);
        }

    }
    @Override
    public void deleteHologram(Player player){
        if(holograms.containsKey(player)){
            holograms.get(player).delete();
            holograms.remove(player);

        }
    }
    @Override
    public void deleteAllHolograms(){
        List<Player> playerList = new ArrayList<>();
        Set<Player> playerSet = holograms.keySet();
        for(Player p:playerSet){
            playerList.add(p);
        }
        for(Player p:playerList){
            deleteHologram(p);

        }
    }

    @Override
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity().getCustomName() == null)return;
        if(event.getEntityType() != EntityType.ZOMBIE)return;
        if(!event.getEntity().getCustomName().equals(name))return;
        if(!((Zombie)event.getEntity()).equals(mobEntity))return;
        if(!isLootDrop){
            event.getDrops().clear();
        }
        if(!isXpDrop){
            event.setDroppedExp(0);
        }
        mobEntity = null;
        boss=null;
        isAlive = false;
        time=time.restart();
        for(Player p:Bukkit.getServer().getOnlinePlayers()){
            createHologram(p);
        }
        for(LivingEntity mob:plugin.mobManager.getMob(0).getEntities().keySet()){
            mob.remove();
        }
        deathMessage();
        addDataToDB();
    }
    private void deathMessage(){
        List<String> message = new ArrayList<>();
        int lines = plugin.getConfig().getConfigurationSection("killboss.messages").getKeys(false).size();

        for(int i=0;i<lines;i++){
            String line =plugin.getConfig().getString(String.format("killboss.messages.line%d",i));
            List<Player> playerList = new ArrayList<>();
            Set<Player> playerSet = playerBossDamage.keySet();
            for(Player p:playerSet){
                playerList.add(p);
            }
            for(int i1=1;i1<plugin.getConfig().getInt("killboss.topcount")+1;i1++){
                String playerReplacement = "%player%N%";
                String damageReplacement = "%damage%N%";
                playerReplacement = playerReplacement.replaceAll("%N", String.valueOf(i1));
                damageReplacement = damageReplacement.replaceAll("%N", String.valueOf(i1));
                if(playerBossDamage.size() < i1){
                    if(line.contains(playerReplacement) || line.contains(damageReplacement)){
                        line = plugin.getConfig().getString("killboss.killernull");
                    }
                }else{

                    line = line.replaceAll(playerReplacement,playerList.get(i1-1).getName());
                    line = line.replaceAll(damageReplacement, String.valueOf(Math.round(playerBossDamage.get(playerList.get(i1-1)))));
                }

            }
            line = line.replaceAll("%boss_name%",name);
            line = line.replace("&","\u00a7");
            message.add(line);
        }
        for(Player p: Bukkit.getServer().getOnlinePlayers()){
            for(String line:message){
                p.sendMessage(line);
            }
        }
    }
    @Override
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Zombie))return;
        if(((Zombie)event.getEntity()).equals(mobEntity)){
            bossHealth.onDamage(event);
        }
    }

    @Override
    public void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Zombie))return;
        if(((Zombie)event.getEntity()).equals(mobEntity)){
            if(event.getDamager() instanceof Player){
                String message =getStringBossHealth(bossHealth);
                Player p= (Player) event.getDamager();
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
                double damage = event.getFinalDamage()/bossHealth.getDamageConst();
                if(playerBossDamage.containsKey(p)){
                    double prevValue =  playerBossDamage.get(p);
                    double value = prevValue+damage;
                    if(value > bossHealth.getMaxHealth()){
                        value=bossHealth.getMaxHealth();
                    }
                    playerBossDamage.replace(p,value);
                }else{
                    playerBossDamage.put(p,damage);
                }
            }
        }
    }

    @Override
    public void onSpawnBoss(CreatureSpawnEvent event) {
        if(event.getEntity().getCustomName() == null)return;
        if(event.getEntityType() != EntityType.ZOMBIE)return;
        if(!event.getEntity().getCustomName().equals(name))return;
        if(isAlive){
            mobEntity.remove();
            mobEntity=null;
            boss=null;
        }
        mobEntity = (Zombie) event.getEntity();
        boss = castToBoss(event.getEntity());
        bossHealth = new BossHealth(Maxhealth,Maxhealth,(LivingEntity) mobEntity);
        deleteAllHolograms();
        isAlive = true;
        Main.getScheduler().runTaskLater(plugin,spawnZombies(),20*data.getInt("skills.spawn_zombies.cooldown"));
        Main.getScheduler().runTaskLater(plugin,equipArmor(),20*data.getInt("skills.equip_armor.cooldown"));

    }

    @Override
    public void onCombust(EntityCombustEvent event) {

        if(!(event.getEntity() instanceof Zombie))return;
        if(event.getEntity().getCustomName() == null)return;
        if(!event.getEntity().getCustomName().equals(name))return;
        if(((Zombie)event.getEntity()).equals(mobEntity)){
            if(!isCombust){
                event.setCancelled(true);
            }
        }
    }

    private ZombieBoss castToBoss(LivingEntity entity){
        Zombie z = (Zombie) entity;
        CraftZombie zC = (CraftZombie) z;
        Entity zE = zC.getHandle();
        return (ZombieBoss) zE;
    }

    @Override
    public void spawnBoss() {
        if(isAlive){
            mobEntity.remove();
            mobEntity=null;
            boss =null;
            isAlive=false;
        }
        worldServer.addEntity(new ZombieBoss(worldServer));

    }
    @Override
    public void spawnBoss(Location location) {
        if(isAlive){
            mobEntity.remove();
            mobEntity=null;
            boss =null;
            isAlive=false;
        }
        ZombieBoss zombie = new ZombieBoss(worldServer);
        zombie.setPosition(location.getX(),location.getY(),location.getZ());
        worldServer.addEntity(zombie);
    }

    @Override
    public boolean isSpawn() {
        return isAlive;
    }

    @Override
    public void deleteBoss() {
        if(isAlive){
            mobEntity.remove();
            mobEntity = null;
            boss= null;
            isAlive = false;
            for(LivingEntity mob:plugin.mobManager.getMob(0).getEntities().keySet()){
                mob.remove();
            }
            time=time.restart();
            for(Player p:Bukkit.getServer().getOnlinePlayers()){
                createHologram(p);
            }
        }
    }

    private Runnable spawnZombies(){
        return new Runnable() {
            @Override
            public void run() {
                if(boss != null){ // check if boss is alive
                    //spawn baby zombies
                    int a=1;
                    int b=3;
                    int diff = b-a;
                    Random random = new Random();
                    int rand = random.nextInt(diff + 1);
                    rand += a;
                    for(int i=0;i<rand;i++){
                        plugin.mobManager.getMob(0).spawnEntityMob(mobEntity.getLocation());
                    }
                    Main.getScheduler().runTaskLater(plugin,spawnZombies(),20*data.getInt("skills.spawn_zombies.cooldown"));
                }
            }
        };
    }
    private Runnable equipArmor(){
        return new Runnable() {
            @Override
            public void run() {
                if(boss != null){
                    EquipmentSlot[] equip = {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.HAND};
                    for(int i=0;i<5;i++){
                        mobEntity.getEquipment().setItem(equip[i],armor.get(Armor.values()[i]));
                    }
                    Main.getScheduler().runTaskLater(plugin,unEquipArmor(),20*data.getInt("skills.equip_armor.skill_time"));

                }
            }
        };
    }
    private Runnable unEquipArmor(){
        return new Runnable() {
            @Override
            public void run() {
                if(boss != null){
                    for(EquipmentSlot equip: EquipmentSlot.values()){
                        mobEntity.getEquipment().setItem(equip,null);
                    }
                    Main.getScheduler().runTaskLater(plugin,equipArmor(),20*data.getInt("skills.equip_armor.cooldown"));
                }


            }
        };
    }


}

enum Armor{
    HELMET,CHESTPLATE,LEGGINGS,BOOTS,SWORD
}

enum Skills{
    SPAWN_ZOMBIES,EQUIP_ARMOR
}