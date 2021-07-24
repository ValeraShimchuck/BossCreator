package com.BossCreator.Bosses.utils;

import com.BossCreator.Main;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class BossHealth {
    private double health;
    private double maxHealth;
    private LivingEntity entity;
    private double entityMaxHealth;
    private double damageConst;
    public BossHealth(double health, double maxHealth, LivingEntity entity){
        this.health = health;
        this.maxHealth=maxHealth;
        this.entity=entity;
        entityMaxHealth = entity.getHealth();
        damageConst = entityMaxHealth/maxHealth;

    }
    public void onDamage(EntityDamageEvent event){
        double Damage = event.getDamage();
        double entityDamage =damageConst*Damage;
        event.setDamage(entityDamage);
        health = (((LivingEntity)event.getEntity()).getHealth()-event.getDamage())/damageConst;

    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }
    public double getDamageConst(){return damageConst;}
    public double getEntityMaxHealth() {
        return entityMaxHealth;
    }
}
