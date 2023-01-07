package net.xrayfun.parkour;

import net.xrayfun.parkour.data.DataBaseManager;
import net.xrayfun.parkour.data.ParkourSettings;
import net.xrayfun.parkour.data.ScoreboardStat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static net.xrayfun.parkour.Main.getInstance;

public class Events implements Listener {
    private static final Random rand = new Random();
    private static List<ParkourSession> parkourGenerators = new ArrayList<>();
    public static HashMap<Player, ScoreboardStat> scoreboardStats = new HashMap<>();

    public Events() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (ParkourSession session : parkourGenerators) {
                        Player player = session.getPlayer();
                        if (inactiveParkourSession(player)) continue;
                        if (session.isFirstGen())
                            firstGen(session, player);
                        if (session.isSecondGen() && !session.isFirstGen())
                            secondGen(session);
                        if (!session.isSecondGen() && !session.isFirstGen())
                            generator(session, player);
                    }
                } catch (Exception e) {

                }
            }
        }.runTaskTimer(getInstance(), 0, 2);

    }

    public static int getRandInt(int min, int max) {
        return rand.nextInt(Math.abs(max - min)) + min;
    }

    private boolean inactiveParkourSession(Player player) {
        try {
            return !ParkourManager.getSession(player).isActiveSession();
        } catch (Exception e) {
            parkourGenerators.remove(ParkourManager.getSession(player));
            return true;
        }
    }

    private void firstGen(ParkourSession session, Player player) {
        scoreboardStats.put(player, new ScoreboardStat(player));
        Location current = new Location(session.getMaxPos().getWorld(),
                getRandInt(session.getMinPos().getBlockX(), session.getMaxPos().getBlockX()),
                getRandInt(session.getMinPos().getBlockY(), session.getMaxPos().getBlockY()),
                getRandInt(session.getMinPos().getBlockZ(), session.getMaxPos().getBlockZ()));
        if (session.generationChecker(current)) {
            session.setCurrent(current);
            current.getBlock().setType(Material.CONCRETE);
            //noinspection deprecation
            current.getBlock().setData((byte) getRandInt(0,16));
            player.teleport(current.clone().add(0.5,2,0.5));
            session.setFirstGen(false);
        }
    }

    private void secondGen(ParkourSession session) {
        Location current = session.getCurrent();
        Location next = new Location(current.getWorld(),
                current.getBlockX() + getRandInt(-3, 4),
                current.getBlockY() + getRandInt(-1, 2),
                current.getBlockZ() + getRandInt(-3, 4));
        if (session.generationChecker(next)) {
            session.setNext(next);
            next.getBlock().setType(Material.CONCRETE);
            //noinspection deprecation
            next.getBlock().setData((byte) getRandInt(0,16));
            session.setRemovePreviousGen(true);
            session.setSecondGen(false);
        }
    }

    private void generator(ParkourSession session, Player player) throws SQLException {
        Location bPL = player.getLocation().clone();
        Location bNL = session.getNext().clone();
        int save = DataBaseManager.getDataScore(session.getSettings().getName(), player);
        int sess = session.getScore();
        if (bPL.getBlockY() < bNL.getBlockY()) {
            if(session.getSettings().getDifficult() == ParkourSettings.Difficult.HARD) {
                player.teleport(session.getCurrent().clone().add(0.5,2,0.5));
                DataBaseManager.setData(session.getSettings().getName(), player, Math.max(save, sess));
                session.setScore(0);
                scoreboardStats.get(player).update(player);
            }
            if(session.getSettings().getDifficult() == ParkourSettings.Difficult.INFINITY) {
                DataBaseManager.setData(session.getSettings().getName(), player, Math.max(save, sess));
                player.teleport(session.getCurrent().clone().add(0.5,2,0.5));
                session.setScore((session.getScore() >= 5 ? session.getScore() - 5 : 0));
                scoreboardStats.get(player).update(player);
            }
        }
        if (bPL.getBlockX() == bNL.getBlockX() && bPL.getBlockZ() == bNL.getBlockZ()) {
            if (session.isRemovePreviousGen()) {
                session.getCurrent().getBlock().setType(Material.AIR);
            }
            session.addScore(1); sess++;
            DataBaseManager.setData(session.getSettings().getName(), player, Math.max(save, sess));
            session.setCurrent(session.getNext());
            scoreboardStats.get(player).update(player);
            session.setSecondGen(true);
        }
    }

    public static void setParkourGenerators(List<ParkourSession> parkourGenerators) {
        Events.parkourGenerators = parkourGenerators;
    }

    public static void addParkourGenerators(ParkourSession session) {
        Events.parkourGenerators.add(session);
    }

    public static void removeParkourGenerators(ParkourSession session) {
        Events.parkourGenerators.remove(session);
    }

    public static HashMap<Player, ScoreboardStat> getScoreboardStats() {
        return scoreboardStats;
    }

    public static void setScoreboardStats(HashMap<Player, ScoreboardStat> scoreboardStats) {
        Events.scoreboardStats = scoreboardStats;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (inactiveParkourSession(player)) return;
        //noinspection SuspiciousMethodCalls
        parkourGenerators.remove(player);
        ParkourManager.stopSession(false, player);
    }
}
