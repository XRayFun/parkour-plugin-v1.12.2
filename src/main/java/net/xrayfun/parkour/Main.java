package net.xrayfun.parkour;

import net.xrayfun.parkour.commands.*;
import net.xrayfun.parkour.data.DataBaseManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;

import static net.xrayfun.parkour.Events.scoreboardStats;

public class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        try {
            DataBaseManager.getOrMakeInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        getServer().getPluginManager().registerEvents(new Events(), this);
        ParkourManager.getOrMakeInstance();
        getServer().getPluginCommand("parkour").setExecutor(new ParkourCommand());
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        Events.setParkourGenerators(new ArrayList<>());
        Object[] players = ParkourManager.sessions.keySet().toArray();
        for (Object player : players) {
            try {
                ParkourManager.stopSession(false, (Player) player);
                scoreboardStats.get((Player) player).remove((Player) player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
