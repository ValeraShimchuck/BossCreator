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
import net.minecraft.server.v1_16_R3.Entity;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPillager;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class IlagerBoss extends EntityPillager implements IBoss {
    private File datafile;
    private FileConfiguration data;
    private Main plugin;
    private Location loc;
    private String name;
    private Double damage;
    private static Double maxHealth;
    private BossTime time;
    private WorldServer worldServer;
    private boolean isCombust;
    private boolean isLootDrop;
    private boolean isXpDrop;
    private boolean isAlive = false;
    private BossHealth bossHealth;
    private HashMap<Skills,Integer> cooldown = new HashMap<>();
    private HashMap<Weapons,ItemStack> weapons = new HashMap<>();
    private HashMap<Player, Hologram> holograms = new HashMap<>();
    private HashMap<Player,Double> playerBossDamage = new HashMap<>();
    private PotionEffect potionEffect;
    private IlagerBoss boss=null;
    private Pillager mobEntity=null;
    private boolean isHalf;
    public IlagerBoss(WorldServer world) {
        super(EntityTypes.PILLAGER, world);
        worldServer = world;
        plugin=Main.getInstance();
        datafile = new File(plugin.getDataFolder()+File.separator+"bosses"+File.separator+String.format("boss%d.yml",getID()));
        createFile();
        data = YamlConfiguration.loadConfiguration(datafile);
        initVariable();
        initWeapon();
        this.setPosition(loc.getX(),loc.getY(),loc.getZ());
        this.setCustomName(new ChatComponentText(name));
        this.setCustomNameVisible(true);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(damage);
        LivingEntity entity = (LivingEntity) this.getBukkitEntity();
        entity.setRemoveWhenFarAway(false);
        this.setCanPickupLoot(false);

        cooldown.put(Skills.DASH,data.getInt("skills.dash.cooldown"));
        potionEffect=new PotionEffect(PotionEffectType.getByName(data.getString("skills.dash.potion.effect")),
                data.getInt("skills.dash.potion.time")*20,
                data.getInt("skills.dash.potion.lvl"));
        ((LivingEntity) this.getBukkitEntity()).getEquipment().setItem(EquipmentSlot.HAND,weapons.get(Weapons.CROSSBOW));


    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.targetSelector.a(2, new PathfinderGoalMeleeAttack(this,1.0D,false));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 15.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 15.0F));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a(new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityVillagerAbstract.class, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, true));
    }

    private void initWeapon() {
        for(Weapons weapon:Weapons.values()){
            String weaponsCategory = weapon.toString().toLowerCase();
            Material weaponType = Material.valueOf(data.getString(String.format("items.%s.item",weaponsCategory)).toUpperCase());
            ItemStack weaponItem = new ItemStack(weaponType);
            ItemMeta meta = weaponItem.getItemMeta();
            int enchants = data.getConfigurationSection(String.format("items.%s.enchants",weaponsCategory)).getKeys(false).size();
            for(int i=0;i<enchants;i++){
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(data.getString(String.format("items.%s.enchants.enchant%d",weaponsCategory,i))));
                int enchantmentLevel = data.getInt(String.format("items.%s.enchantlvl.lvl%d",weaponsCategory,i));
                meta.addEnchant(enchantment,enchantmentLevel,true);
            }
            meta.setUnbreakable(data.getBoolean(String.format("items.%s.breakable",weaponsCategory)));
            weaponItem.setItemMeta(meta);
            weapons.put(weapon,weaponItem);

        }
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
        maxHealth = data.getDouble("params.maxhealth");
        isCombust = data.getBoolean("params.combust");
        isLootDrop = data.getBoolean("params.lootdrop");
        isXpDrop = data.getBoolean("params.xpdrop");
        int hours = data.getInt("time.hours");
        int minutes = data.getInt("time.minutes");
        int seconds = data.getInt("time.seconds");
        time = new BossTime(hours,minutes,seconds,this);

    }
    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void createFile() {
        if(!datafile.exists()){
            try {
                datafile.createNewFile();
                data = YamlConfiguration.loadConfiguration(datafile);
                data.set("name","Разоритель");
                data.set("location.x",0);
                data.set("location.y",0);
                data.set("location.z",0);
                data.set("location.world","world");
                data.createSection("params");
                data.set("params.damage",1);
                data.set("params.maxhealth",20);
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
        data.set("items.crossbow.item",Material.CROSSBOW.getKey().getKey());
        data.set("items.crossbow.enchants.enchant0","multishot");
        data.set("items.crossbow.enchants.enchant1","piercing");
        data.set("items.crossbow.enchantlvl.lvl0",1);
        data.set("items.crossbow.enchantlvl.lvl1",4);
        data.set("items.crossbow.breakable",false);

        data.set("items.axe.item",Material.IRON_AXE.getKey().getKey());
        data.set("items.axe.breakable",false);
        data.createSection("items.axe.enchants");
        data.createSection("items.axe.enchantlvl");

        data.set("skills.dash.cooldown",60);
        data.set("skills.dash.potion.effect",PotionEffectType.INCREASE_DAMAGE.getName());
        data.set("skills.dash.potion.lvl",1);
        data.set("skills.dash.potion.time",4);

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
    public String getName(){
        return name;
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
    public BossTime getTime() {
        return time;
    }

    @Override
    public IBoss rebuild() {
        WorldServer world =((CraftWorld)loc.getWorld()).getHandle();
        return new IlagerBoss(world);
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
    public void deleteHologram(Player player) {
        if(holograms.containsKey(player)){
            holograms.get(player).delete();
            holograms.remove(player);
        }
    }

    @Override
    public void deleteAllHolograms() {
        for(Player p:holograms.keySet()){
            deleteHologram(p);
        }
    }

    @Override
    public void onDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Pillager))return;
        if(((Pillager)event.getEntity()).equals(mobEntity)){
            if(!isLootDrop){
                event.getDrops().clear();
            }
            if(!isXpDrop){
                event.setDroppedExp(0);
            }
            boss=null;
            isAlive = false;
            time=time.restart();
            for(Player p:Bukkit.getServer().getOnlinePlayers()){
                createHologram(p);
            }
            playerBossDamage = getSortedPlayers(playerBossDamage);
            deathMessage();
            addDataToDB();
        }
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
                    line = line.replace("&","\u00a7");
                }

            }
            line = line.replaceAll("%boss_name%",name);
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
        if(!(event.getEntity() instanceof Pillager))return;
        //if(boss == null)return;
        if(((Pillager)event.getEntity()).equals(mobEntity)){
            bossHealth.onDamage(event);
            if(bossHealth.getMaxHealth()/2 >= bossHealth.getHealth() && !isHalf){
                isHalf=true;
                changeWeapon();
                Main.getScheduler().runTaskLater(plugin,dash(),20*data.getInt("skills.dash.cooldown"));
            }
        }
    }

    @Override
    public void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Pillager))return;
        if(((Pillager)event.getEntity()).equals(mobEntity)){
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


    private void changeWeapon(){
        boss.initPathfinder();
        ((LivingEntity) boss.getBukkitEntity()).getEquipment().setItem(EquipmentSlot.HAND,weapons.get(Weapons.AXE));
        /*
        После смены оружия у разбойника(когда он перезаряжал арбалет) может появиться визуальный баг, который я пытался пофиксить, но не получилось
        */
    }
    private Runnable dash(){
        return new Runnable() {
            @Override
            public void run() {
                if(isAlive){
                    if(isHalf){
                        mobEntity.addPotionEffect(potionEffect);
                        List<org.bukkit.entity.Entity> mobs = mobEntity.getNearbyEntities(6, 2, 6);
                        if(mobs.size() >0){
                            for(org.bukkit.entity.Entity entity:mobs){
                                if(entity instanceof Player){

                                    Location location = entity.getLocation();
                                    location.setY(mobEntity.getLocation().getY());
                                    walkToLocation(mobEntity,location,1.0D);

                                }
                            }

                        }
                        Main.getScheduler().runTaskLater(plugin,dash(),20*data.getInt("skills.dash.cooldown"));
                    }
                }

            }
        };
    }

    @Override
    public void onSpawnBoss(CreatureSpawnEvent event) {
        if(event.getEntity().getCustomName() == null)return;
        if(event.getEntityType() != EntityType.PILLAGER)return;
        if(!event.getEntity().getCustomName().equals(name))return;
        if(isAlive){
            mobEntity.remove();
            mobEntity=null;
            boss=null;
        }
        mobEntity = (Pillager) event.getEntity();
        boss = castToBoss(event.getEntity());
        bossHealth = new BossHealth(maxHealth,maxHealth,(LivingEntity) boss.getBukkitEntity());
        deleteAllHolograms();
        isAlive=true;
        isHalf=false;
        playerBossDamage = new HashMap<>();
        //add schedulers

    }
    private IlagerBoss castToBoss(LivingEntity entity){
        Pillager p = (Pillager) entity;
        CraftPillager pC = (CraftPillager) p;
        Entity pE = pC.getHandle();
        return (IlagerBoss) pE;
    }

    @Override
    public void onCombust(EntityCombustEvent event) {
        if(!(event.getEntity() instanceof Pillager))return;
        if(event.getEntity().getCustomName() == null)return;
        if(!event.getEntity().getCustomName().equals(name))return;
        if(((Pillager)event.getEntity()).equals(mobEntity)){
            if(!isCombust){
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void spawnBoss() {
        if(isAlive){
            boss.getBukkitEntity().remove();
            boss =null;
        }
        worldServer.addEntity(new IlagerBoss(worldServer));
    }

    @Override
    public void spawnBoss(Location location) {
        if(isAlive){
            boss.getBukkitEntity().remove();
            boss =null;
        }
        IlagerBoss pillager = new IlagerBoss(worldServer);
        pillager.setPosition(location.getX(),location.getY(),location.getZ());
        worldServer.addEntity(new IlagerBoss(worldServer));
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

        }
    }
    public static float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }
    public void walkToLocation(LivingEntity entity, Location location, double speed) {
        new BukkitRunnable() {
            public void run() {
                if(entity.getLocation().distance(location) > 0.3) {
                    float yaw = getRotations(entity.getLocation(), location)[0];
                    Vector direction = new Vector(-Math.sin(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F).multiply(speed);

                    if(entity.getLocation().getY() - location.getY() > 0 && entity.isOnGround()) {
                        direction.setY(Math.min(0.42, entity.getLocation().getY() - location.getY()));
                    }
                    entity.setVelocity(direction);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1L);
    }
    enum Weapons{
        CROSSBOW, AXE
    }
    enum Skills{
        CHANGE_WEAPON,DASH
    }
}
