package net.xrayfun.parkour;

import net.xrayfun.parkour.data.ParkourSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ParkourSession {
    private final Player player;
    private final ParkourSettings settings;
    private boolean activeSession;
    private int score;
    private Location maxPos, minPos;
    private Location current, next;
    private boolean firstGen, secondGen, removePreviousGen;

    public ParkourSession(Player player, ParkourSettings settings) {
        this.player = player;
        this.settings = settings;
        this.activeSession = true;

        this.score = 0;

        maxPos = this.settings.getPos1();
        minPos = this.settings.getPos2();
        Location temp1 = new Location(maxPos.getWorld(),
                Math.max(maxPos.getBlockX(), minPos.getBlockX()),
                Math.max(maxPos.getBlockY(), minPos.getBlockY()),
                Math.max(maxPos.getBlockZ(), minPos.getBlockZ()));
        Location temp2 = new Location(minPos.getWorld(),
                Math.min(maxPos.getBlockX(), minPos.getBlockX()),
                Math.min(maxPos.getBlockY(), minPos.getBlockY()),
                Math.min(maxPos.getBlockZ(), minPos.getBlockZ()));
        maxPos = temp1;
        minPos = temp2;

        setFirstGen(true);
        setSecondGen(true);
        setRemovePreviousGen(false);
    }

    public boolean generationChecker(Location loc) {
        boolean valid1 = !(loc.getBlockX() > maxPos.getBlockX() || loc.getBlockX() < minPos.getBlockX() ||
                loc.getBlockY() > maxPos.getBlockY() || loc.getBlockY() < minPos.getBlockY() ||
                loc.getBlockZ() > maxPos.getBlockZ() || loc.getBlockZ() < minPos.getBlockZ());
        boolean valid2 = !(loc.clone().add(0, 0, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, 1, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, 2, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, 3, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, -1, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, -2, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, -3, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(1, 0, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(2, 0, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(-1, 0, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(-2, 0, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, 0, 1).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, 0, 2).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, 0, -1).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, 0, -2).getBlock().getType() != Material.AIR ||
                loc.clone().add(1, 0, 1).getBlock().getType() != Material.AIR ||
                loc.clone().add(-1, 0, 1).getBlock().getType() != Material.AIR ||
                loc.clone().add(1, 0, -1).getBlock().getType() != Material.AIR ||
                loc.clone().add(-1, 0, -1).getBlock().getType() != Material.AIR ||
                loc.clone().add(1, 1, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(-1, 1, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, 1, 1).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, 1, -1).getBlock().getType() != Material.AIR ||
                loc.clone().add(1, 1, 1).getBlock().getType() != Material.AIR ||
                loc.clone().add(-1, 1, 1).getBlock().getType() != Material.AIR ||
                loc.clone().add(1, 1, -1).getBlock().getType() != Material.AIR ||
                loc.clone().add(-1, 1, -1).getBlock().getType() != Material.AIR ||
                loc.clone().add(1, -1, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(-1, -1, 0).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, -1, 1).getBlock().getType() != Material.AIR ||
                loc.clone().add(0, -1, -1).getBlock().getType() != Material.AIR ||
                loc.clone().add(1, -1, 1).getBlock().getType() != Material.AIR ||
                loc.clone().add(-1, -1, 1).getBlock().getType() != Material.AIR ||
                loc.clone().add(1, -1, -1).getBlock().getType() != Material.AIR ||
                loc.clone().add(-1, -1, -1).getBlock().getType() != Material.AIR);
        return valid1 && valid2;
    }



    /**
     * <p> Получение текущего счета в сессии.
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * <p> Установка текущего счета в сессии.
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * <p> Увеличение текущего счета в сессии.
     */
    public void addScore(int score) {
        this.score += score;
    }

    /**
     * <p> Получение игрока из сессии.
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    public ParkourSettings getSettings() {
        return settings;
    }

    public Location getMaxPos() {
        return maxPos;
    }

    public Location getMinPos() {
        return minPos;
    }

    public Location getCurrent() {
        return current;
    }

    public void setCurrent(Location current) {
        this.current = current;
    }

    public Location getNext() {
        return next;
    }

    public void setNext(Location next) {
        this.next = next;
    }

    /**
     * <p> Определение первой генерации в сессии.
     * @return boolean of <b>firstGen</b>
     */
    public boolean isFirstGen() {
        return firstGen;
    }

    public void setFirstGen(boolean firstGen) {
        this.firstGen = firstGen;
    }

    public boolean isSecondGen() {
        return secondGen;
    }

    public void setSecondGen(boolean secondGen) {
        this.secondGen = secondGen;
    }

    public boolean isRemovePreviousGen() {
        return removePreviousGen;
    }

    public void setRemovePreviousGen(boolean removePreviousGen) {
        this.removePreviousGen = removePreviousGen;
    }

    public boolean isActiveSession() {
        return activeSession;
    }

    public void setActiveSession(boolean activeSession) {
        this.activeSession = activeSession;
    }
}
