package com.BossCreator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLDatabase {
    private String url;
    public SQLDatabase() throws Exception{
        url="jdbc:sqlite:" + Main.getInstance().getDataFolder()+ File.separator+"database.db";
        Class.forName("org.sqlite.JDBC").newInstance();
    }
    public Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url);
    }

}
