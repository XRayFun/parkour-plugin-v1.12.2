package net.xrayfun.parkour.data;

import net.xrayfun.parkour.Main;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ParkourSettings {
    public enum Difficult {
        INFINITY,
        HARD
    }

    private final String name;
    private Difficult difficult;
    private Location pos1, pos2;
    private Location respawn;

    public ParkourSettings(String name) {
        this.name = name;
        load();
    }

    public ParkourSettings(String name, Difficult difficult, Location pos1, Location pos2, Location respawn) {
        this.name = name;
        this.difficult = difficult;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.respawn = respawn;
    }

    public void save() {
        ConfigurationSection section = Config.getInstance().getLastSection("parkour", name);
        section.createSection("difficult");
        section.set("difficult", difficult.name());
        Config.setLocation(pos1, section.createSection("pos1"));
        Config.setLocation(pos2, section.createSection("pos2"));
        Config.setLocation(respawn, section.createSection("respawn"));
        Main.getInstance().saveConfig();
    }

    public void load() {
        ConfigurationSection section = Config.getInstance().getLastSection("parkour", name);
        difficult = Difficult.valueOf(section.getString("difficult"));
        pos1 = Config.getLocation(0, false, section.getConfigurationSection("pos1"));
        pos2 = Config.getLocation(0, false, section.getConfigurationSection("pos2"));
        respawn = Config.getLocation(0, true, section.getConfigurationSection("respawn"));
    }

    public static boolean validSettings(String name, List<String> params) {
        try {
            return Config.getInstance().getLastSection("parkour", name).getKeys(false).containsAll(params);
        } catch (Exception e) {
            return false;
        }
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public Location getRespawn() {
        return respawn;
    }

    public String getName() {
        return name;
    }

    public Difficult getDifficult() {
        return difficult;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
        save();
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
        save();
    }

    public void setRespawn(Location respawn) {
        this.respawn = respawn;
        save();
    }

    public void setDifficult(Difficult difficult) {
        this.difficult = difficult;
        save();
    }
}
