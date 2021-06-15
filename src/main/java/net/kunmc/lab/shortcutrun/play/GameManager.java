package net.kunmc.lab.shortcutrun.play;

import net.kunmc.lab.shortcutrun.core.Stage;
import net.kunmc.lab.shortcutrun.exception.PlayException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class GameManager extends BukkitRunnable {

    private static final String SCOREBOARD_NAME = "footings";

    private List<Location> placedFootingLocations = new ArrayList<>();
    private Stage stage;
    private boolean playing = false;

    public void start() throws PlayException {

        if (stage == null) throw new PlayException("ステージが選択されていません！");

        if (isPlaying()) throw new PlayException("既にゲームが開始しています！");

        playing = true;
    }

    public void end() {

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void placeFooting(Player player, Block block) {
        block.setType(Material.OAK_PLANKS);
        placedFootingLocations.add(block.getLocation());
    }

    public int getFooting(Player player) {
        return getScoreboardObjective().getScore(player.getName()).getScore();
    }

    public void setFooting(Player player, int value) {
        getScoreboardObjective().getScore(player.getName()).setScore(value);
    }

    public Objective getScoreboardObjective() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective(SCOREBOARD_NAME);
        if (objective == null) {
            scoreboard.registerNewObjective(SCOREBOARD_NAME, "", SCOREBOARD_NAME);
        }
        return scoreboard.getObjective(SCOREBOARD_NAME);
    }

    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void run() {
        if (!isPlaying()) {
            return;
        }
        Bukkit.getOnlinePlayers().forEach(player -> {
            int footings = getFooting(player);
            player.sendActionBar(String.valueOf(footings));
        });
    }
}
