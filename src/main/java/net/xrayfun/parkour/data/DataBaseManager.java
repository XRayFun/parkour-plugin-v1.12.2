package net.xrayfun.parkour.data;

import net.xrayfun.parkour.Main;
import net.xrayfun.parkour.ParkourManager;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.*;

public class DataBaseManager {
    private static final String filePath = Main.getInstance().getDataFolder() + "/top.db";
    private static final String url = "jdbc:sqlite:" + filePath;
    private static DataBaseManager instance;

    public DataBaseManager() {
        File dbFile = new File(filePath);
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

    public static void insertData(String parkourName, Player player, String... values) throws SQLException {
        Connection con = getConnection();
        Statement stmt = con.createStatement();

        stmt.execute("INSERT INTO " + parkourName + " VALUES ('" + player.getName() + "', " + String.join(", ", values) + ");");
        stmt.close();
    }

    public static void setData(String parkourName, Player player, String[] values, String[] types) throws SQLException {
        if(values.length != types.length) return;
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        StringBuilder sets = new StringBuilder();
        for (int i = 0; i < values.length; i++){
            if(i != 0)
                sets.append(", ");
            sets.append(types[i]).append("=").append(values[i]);
        }
        try {
            stmt.execute("UPDATE " + parkourName + " SET " + sets + " WHERE nick = '" + player.getName() + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            insertData(parkourName, player, values);
        }
        stmt.close();
    }

    public static int getData(String parkourName, Player player, String column) {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery("SELECT " + column + " FROM " + parkourName + " WHERE nick = '" + player.getName() + "';");
            if (!result.next()) {
                insertData(parkourName, player, String.valueOf(ParkourManager.getSession(player).getScore()));
                result = stmt.executeQuery("SELECT " + column + " FROM " + parkourName + " WHERE nick = '" + player.getName() + "';");
            }
            int data = result.getInt(column);
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
