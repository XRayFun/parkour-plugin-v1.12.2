package net.xrayfun.parkour.data;

import net.xrayfun.parkour.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static Config instance;
    private final FileConfiguration config;

    public Config(FileConfiguration config) {
        this.config = config;
    }

    public static Config getInstance() {
        if (instance == null)
            instance = new Config(Main.getInstance().getConfig());
        return instance;
    }

    public void setLocation(Location loc, String... path) {
        ConfigurationSection section = getLastSection(path);
        setLocation(loc, section);
    }

    public static void setLocation(Location loc, ConfigurationSection section) {
        section.set("world", loc.getWorld().getName());
        section.set("x", Math.round(loc.getX()));
        section.set("y", Math.round(loc.getY()));
        section.set("z", Math.round(loc.getZ()));
        Main.getInstance().saveConfig();
    }

    public Location getLocation(String... path) {
        return getLocation(0, false, path);
    }

    public Location getLocation(boolean rotation, String... path) {
        return getLocation(0, rotation, path);
    }

    public Location getLocation(int num, boolean rotation, String... path) {
        ConfigurationSection section = getLastSection(path);
        return getLocation(num, rotation, section);
    }

    public static Location getLocation(int num, boolean rotation, ConfigurationSection section, String... path) {
        section = getLastSection(section, path);
        return getLocation(num, rotation, section);
    }

    public static Location getLocation(int num, boolean rotation, ConfigurationSection section) {
        String n = num == 0 ? "" : String.valueOf(num);
        String worldName = section.getString("world" + n);
        if (worldName == null) worldName = "world";
        World world = Bukkit.getWorld(worldName);
        double x = section.getDouble("x" + n);
        double y = section.getDouble("y" + n);
        double z = section.getDouble("z" + n);
        Location loc = new Location(world, x, y, z);
        if (rotation) {
            loc.setYaw((float) section.getDouble("yaw" + n));
            loc.setPitch((float) section.getDouble("pitch" + n));
        }
        return loc;
    }

    public ConfigurationSection getLastSection(String... path) {
        ConfigurationSection section = null;
        for (String p : path) {
            if (section == null) {
                section = config.getConfigurationSection(p);
                if (section == null)
                    section = config.createSection(p);
            } else {
                ConfigurationSection s = section.getConfigurationSection(p);
                if (s == null)
                    s = section.createSection(p);
                section = s;
            }
        }
        Main.getInstance().saveConfig();
        return section;
    }

    public static ConfigurationSection getLastSection(ConfigurationSection section, String... path) {
        for (String p : path) {
            ConfigurationSection s = section.getConfigurationSection(p);
            if (s == null)
                s = section.createSection(p);
            section = s;
        }
        Main.getInstance().saveConfig();
        return section;
    }
}
