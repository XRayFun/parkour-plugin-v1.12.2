package net.xrayfun.parkour.data;

import net.xrayfun.parkour.Main;
import net.xrayfun.parkour.ParkourManager;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;

public class DataBaseManager {
    private static String url = "jdbc:sqlite:" + Main.getInstance().getDataFolder() + "/top.db";
    private static DataBaseManager instance;

    public DataBaseManager() {
        File dbFile = new File(Main.getInstance().getDataFolder() + "/top.db");
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        try {
            Connection con = getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getOrMakeInstance() throws SQLException {
        if (instance == null) instance = new DataBaseManager();
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public static void makeTable(String parkourName) throws SQLException {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS " + parkourName + " ('nick' TEXT, 'score' INT)");
        stmt.close();
    }

    public static void insertData(String parkourName, Player player, int score) throws SQLException {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.execute("INSERT INTO " + parkourName + " (nick, score) VALUES ('" + player.getName() + "', " + score + ");");
        stmt.close();
    }

    public static void setData(String parkourName, Player player, int score) throws SQLException {
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute("UPDATE " + parkourName + " SET score = " + score + " WHERE nick = '" + player.getName() + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            insertData(parkourName, player, score);
        }
        stmt.close();
    }

    public static int getDataScore(String parkourName, Player player) {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery("SELECT score FROM " + parkourName + " WHERE nick = '" + player.getName() + "';");
            if(!result.next()) {
                insertData(parkourName, player, ParkourManager.getSession(player).getScore());
                result = stmt.executeQuery("SELECT score FROM " + parkourName + " WHERE nick = '" + player.getName() + "';");
            }
            int data = result.getInt("score");
            stmt.close();
            result.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void dropTable(String parkourName) {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();

            stmt.execute("DROP TABLE IF EXISTS " + parkourName + ";");
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
