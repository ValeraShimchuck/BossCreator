package com.BossCreator.mobs;

import com.BossCreator.Bosses.utils.BossHealth;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;

public interface IMob {
    void onDeath(EntityDeathEvent event);
    void onDamage(EntityDamageEvent event);
    void onSpawn(CreatureSpawnEvent event);
    void onCombust(EntityCombustEvent event);
    void spawnEntityMob(Location loc);
    HashMap<LivingEntity, BossHealth> getEntities();
}
