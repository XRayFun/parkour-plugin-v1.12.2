package net.xrayfun.parkour;

import net.xrayfun.parkour.data.Config;
import net.xrayfun.parkour.data.DataBaseManager;
import net.xrayfun.parkour.data.ParkourSettings;
import net.xrayfun.parkour.data.ScoreboardStat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import static net.xrayfun.parkour.Events.*;

public class ParkourManager {
    private static ParkourManager instance;
    public static final HashMap<Player, ParkourSession> sessions = new HashMap<>();
    public static final HashMap<String, ParkourSettings> parkourSettings = new HashMap<>();

    private ParkourManager() {
        loadSettings();
    }

    public static void getOrMakeInstance() {
        if (instance == null) instance = new ParkourManager();
    }

    private void loadSettings() {
        for (String name : Config.getInstance().getLastSection("parkour").getKeys(false)) {
            if (!ParkourSettings.validSettings(name, Arrays.asList("difficult", "pos1", "pos2", "respawn"))) return;
            ParkourSettings settings = new ParkourSettings(name);
            parkourSettings.put(name, settings);
            try {
                DataBaseManager.makeTable(name);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ParkourSettings getSettings(String name) {
        return parkourSettings.getOrDefault(name, null);
    }

    public static void makeSettings(String name, ParkourSettings.Difficult difficult, Location pos1, Location pos2, Location respawn) {
        ParkourSettings settings = new ParkourSettings(name, difficult, pos1, pos2, respawn);
        settings.save();
        parkourSettings.put(name, settings);
        Main.getInstance().saveConfig();
        try {
            DataBaseManager.makeTable(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeSettings(String name) {
        parkourSettings.remove(name);
        Config.getInstance().getLastSection("parkour").set(name, null);
        Main.getInstance().saveConfig();
        DataBaseManager.dropTable(name);
    }

    public static void startSession(Player player, String parkourName) {
        if(!parkourSettings.containsKey(parkourName)) {
            player.sendMessage("Такого паркура не существует!");
            return;
        }
        stopSession(false, player);
        ParkourSettings settings = parkourSettings.get(parkourName);
        ParkourSession session = new ParkourSession(player, settings);
        sessions.put(player, session);
        addParkourGenerators(session);
        player.sendMessage("Сессия запущена!");
    }

    public static ParkourSession getSession(Player player) {
        return sessions.get(player);
    }

    public static void restartSession(Player player) {
        try {
            ParkourSession session = sessions.get(player);
            startSession(player, session.getSettings().getName());
        } catch (Exception e) {
            player.sendMessage("Нет активной сессии!");
        }
    }

    public static void stopSession(boolean msg, Player... players) {
        for(Player player : players) {
            try {
                ParkourSession session = sessions.get(player);
                removeParkourGenerators(sessions.get(player));
                session.setActiveSession(false);
                session.getCurrent().getBlock().setType(Material.AIR);
                session.getNext().getBlock().setType(Material.AIR);
                player.teleport(session.getSettings().getRespawn());

                HashMap<Player, ScoreboardStat> temp = getScoreboardStats();
                temp.get(player).remove(player);
                temp.remove(player);
                setScoreboardStats(temp);
                sessions.remove(player);
                if (msg)
                    player.sendMessage("Сессия закончилась!");
            } catch (Exception e) {
                if (msg)
                    player.sendMessage("Нет активной сессии!");
            }
        }
    }
}
