package net.kunmc.lab.shortcutrun.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import net.kunmc.lab.shortcutrun.exception.ConfigLoadException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MainManager {

    private static final String SCOREBOARD_NAME = "footings";

    private RenderSystem renderSystem = new RenderSystem();

    private Stage stage;
    private boolean editing = false;
    private boolean playing = false;

    private List<Location> placedFootingLocations = new ArrayList<>();

    public void setStage(Stage stage) {
        this.stage = stage;
        renderSystem.setStage(stage);
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

    public void debug() {
        renderSystem.render(Bukkit.getWorlds().get(0));
    }
}
