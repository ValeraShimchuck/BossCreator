package com.BossCreator.mobs;

import com.BossCreator.Bosses.utils.BossHealth;
import com.BossCreator.Main;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;

public class MinionZombie extends EntityZombie implements IMob {
    private Main plugin;
    private FileConfiguration data;
    private double maxHealth;
    private double damage;
    private boolean isCombust;
    private boolean lootDrop;
    private boolean xpDrop;
    private HashMap<LivingEntity,BossHealth> mobHealth = new HashMap<>();
    public MinionZombie(Location loc) {
        super(EntityTypes.ZOMBIE,((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(),loc.getY(),loc.getZ());
        this.setBaby(true);
        plugin=Main.getInstance();
        data = plugin.bossManager.getBoss(0).getConfig();
        init();
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(damage);
        LivingEntity entity = (LivingEntity) this.getBukkitEntity();
        entity.setRemoveWhenFarAway(false);
        this.setCanPickupLoot(false);
        this.initPathfinder();
    }
    @Override
    protected void m(){
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::eU));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[]{EntityPigZombie.class}));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
        //this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityVillagerAbstract.class, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, true));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget(this, EntityTurtle.class, 10, true, false, EntityTurtle.bo));
    }
    private void init(){
        isCombust = data.getBoolean("params.minions_combust");
        lootDrop = data.getBoolean("params.minions_lootdrop");
        xpDrop = data.getBoolean("params.minions_xpdrop");
        maxHealth = data.getDouble("params.minions_health");
        damage = data.getDouble("params.minions_damage");
    }
    @Override
    public void onDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Zombie))return;
        LivingEntity mob = (LivingEntity) event.getEntity();
        if(mobHealth.containsKey(mob)){
              if(!xpDrop){
                  event.setDroppedExp(0);
              }
              if(!lootDrop){
                  event.getDrops().clear();
              }
              mobHealth.remove(mob);
        }
    }

    @Override
    public void onDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Zombie))return;
        LivingEntity mob = (LivingEntity) event.getEntity();
        if(mobHealth.containsKey(mob)){
            mobHealth.get(mob).onDamage(event);
        }
    }

    @Override
    public void onSpawn(CreatureSpawnEvent event) {

    }

    @Override
    public void onCombust(EntityCombustEvent event) {
        if(!(event.getEntity() instanceof Zombie))return;
        LivingEntity mob = (LivingEntity) event.getEntity();
        if(mobHealth.containsKey(mob)){
            if(!isCombust){
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void spawnEntityMob(Location loc) {
        MinionZombie minion = new MinionZombie(loc);
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        world.addEntity(minion);
        mobHealth.put((LivingEntity) minion.getBukkitEntity(),new BossHealth(maxHealth,maxHealth, (LivingEntity) minion.getBukkitEntity()));

    }

    @Override
    public HashMap<LivingEntity, BossHealth> getEntities() {
        return mobHealth;
    }
}
