package com.BossCreator.Bosses.utils;

import com.BossCreator.Bosses.IBoss;
import com.BossCreator.Main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

public class BossTime {
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final long unixTime;
    private final IBoss boss;
    private long killTime;
    private long spawnTime;
    private long timeToSpawn;
    private final long unixTimeToSpawn;
    private final Main plugin=Main.getInstance();
    public BossTime(int hours, int minutes, int seconds, IBoss boss){
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        int unixSeconds = hours *3600+minutes* 60 +seconds;
        spawnTime = unixSeconds;
        this.boss = boss;
        unixTime = Instant.now().getEpochSecond();
        timeToSpawn = spawnTime;
        unixTimeToSpawn = unixTime + timeToSpawn;
    }
    public long getCurrentTime(){return Instant.now().getEpochSecond();}
    public long getUnixTimeToSpawn(){return unixTimeToSpawn;}
    public BossTime restart(){
        return new BossTime(hours,minutes,seconds,boss);
    }
    public boolean isSpawned(){
        return getCurrentTime() >= unixTimeToSpawn;
    }

    public long getTime(){
        return spawnTime;
    }

    public int getCurrentHours() {
        int times = (int) (unixTimeToSpawn-getCurrentTime());
        int hour = times/3600;
        return hour;
    }

    public int getCurrentMinutes() {
        int times = (int) (unixTimeToSpawn-getCurrentTime());

        int hour = times/3600;
        times = times - 3600*hour;
        int mins = times /60;
        return mins;
    }

    public int getCurrentSeconds() {
        int times = (int) (unixTimeToSpawn-getCurrentTime());
        int hour = times/3600;
        times = times - 3600*hour;
        int mins = times /60;
        int sec = times-(60*mins);
        return sec;
    }
}
