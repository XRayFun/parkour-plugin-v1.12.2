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
    private enum Columns{
        nick,
        score
    }

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

    // Checker tableName in ParkourCommand.class (checkValidStringDB(String data))
    public static void makeTable(String tableName) throws SQLException {
        try (Statement stmt = getConnection().createStatement();){
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + " ('nick' TEXT, 'score' INT)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // пользовательский ввод не предусмотрен
    public static void insertData(String tableName, Player player, String... value) throws SQLException {
        String sql = "INSERT INTO " + tableName + " VALUES (?, ?);";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, player.getName());
            stmt.setString(2, String.join(", ", value));
            stmt.executeUpdate();
        }
    }

    // пользовательский ввод не предусмотрен
    public static void setData(String tableName, Player player, String value) throws SQLException {
        String sql = "UPDATE " + tableName + " SET score=? WHERE nick=?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);){
            stmt.setString(1, value);
            stmt.setString(2, player.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            insertData(tableName, player, value);
        }
    }

    // пользовательский ввод не предусмотрен
    public static int getData(String tableName, Player player, String column) {
        String sql = "SELECT " + column + " FROM " + tableName + " WHERE nick=?;";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, player.getName());
            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                insertData(tableName, player, String.valueOf(ParkourManager.getSession(player).getScore()));
                result = stmt.executeQuery();
            }
            int data = result.getInt(column);
            result.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Checker tableName in ParkourCommand.class (checkValidStringDB(String data))
    public static void dropTable(String tableName) {
        try (Statement stmt = getConnection().createStatement()){
            stmt.execute("DROP TABLE IF EXISTS " + tableName + ";");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
