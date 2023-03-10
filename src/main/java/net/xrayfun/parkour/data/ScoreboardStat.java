package net.xrayfun.parkour.data;

import net.xrayfun.parkour.ParkourManager;
import net.xrayfun.parkour.ParkourSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.sql.SQLException;

public class ScoreboardStat {
    private final Objective objective;
    private final ScoreboardManager manager;
    private final Scoreboard scoreboard;
    public ScoreboardStat(Player player) {
        this.manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective(player.getName(), "dummy");

        create(player);
    }

    public void create(Player player) {
        objective.setDisplayName("§f§lParkour Stats");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = objective.getScore("§a§lСчет:");
        score.setScore(ParkourManager.getSession(player).getScore());
        Score top = objective.getScore("§6§lЛучший счет:");
        top.setScore(DataBaseManager.getData(ParkourManager.getSession(player).getSettings().getName(), player, "score"));
        player.setScoreboard(scoreboard);
    }

    public void update(Player player) {
        ParkourSession session = ParkourManager.getSession(player);
        Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore("§a§lСчет:");
        score.setScore(session.getScore());
        score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore("§6§lЛучший счет:");
        score.setScore(DataBaseManager.getData(session.getSettings().getName(), player, "score"));
    }

    public void remove(Player player) throws SQLException {
        player.setScoreboard(manager.getNewScoreboard());
        ParkourSession session = ParkourManager.getSession(player);
        int scoreDB = DataBaseManager.getData(ParkourManager.getSession(player).getSettings().getName(), player, "score");
        if(scoreDB < session.getScore())
            DataBaseManager.setData(session.getSettings().getName(),
                    player,
                    String.valueOf(session.getScore()));
    }
}
