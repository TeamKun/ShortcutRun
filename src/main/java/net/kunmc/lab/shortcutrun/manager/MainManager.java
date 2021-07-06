package net.kunmc.lab.shortcutrun.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.config.Configration;
import net.kunmc.lab.shortcutrun.gameobject.Footing;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MainManager extends BukkitRunnable {

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

    public void unselect() {
        setStage(null);
    }

    public boolean tryPlaceFooting(Location playerLocation) {

        boolean placed = false;

        double radis = Configration.placeFootingDistance.get();

        int blockRadis = Configration.placeFootingBlock.get();
        blockRadis --;

        Location location = playerLocation.clone().add(- blockRadis, 0, - blockRadis);

        for (int offsetX = 0 ; offsetX < 2 * blockRadis + 1 ; offsetX ++) {
            for (int offsetZ = 0 ; offsetZ < 2 * blockRadis + 1 ; offsetZ ++) {

                Block block = location.clone().add(offsetX, 0, offsetZ).getBlock();

                if (!block.getType().equals(Material.AIR)) {
                    continue;
                }

                if (block.getLocation().clone().add(0.5, 0, 0.5).distance(playerLocation) > radis) {
                    continue;
                }

                block.setType(Material.OAK_PLANKS);
                placedFootingLocations.add(block.getLocation());

                placed = placed || true;
            }
        }

        return placed;
    }

    public int getFootingAmount(Player player) {
        return getScoreboardObjective().getScore(player.getName()).getScore();
    }

    public void setFootingAmount(Player player, int value) {
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

    public boolean isEditing() { return editing; }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public Stage getSelectedStage() {
        return stage;
    }

    // 設置された足場を全て空気ブロックに戻して、足場エンティティを削除

    public void reset() {

        new BukkitRunnable() {
            @Override
            public void run() {
                new ArrayList<>(placedFootingLocations).forEach(location -> location.getBlock().setType(Material.AIR));
            }
        }.run();

        placedFootingLocations.clear();

        stage.resetFooting();

        if (Configration.resetOnFinish.get()) {

            Bukkit.getOnlinePlayers().forEach(player -> setFootingAmount(player, 0));

        }

    }

    @Override
    public void run() {

        // プレイ中の足場の復活

        if (isPlaying()) {

            List<Footing> pickedUpFootings = stage.footings.stream().filter(footing -> footing.isPickedUp()).collect(Collectors.toList());
            Collections.shuffle(pickedUpFootings);

            int resetCount = Math.min(Configration.footingRestorePerTick.get(), pickedUpFootings.size());
            for (int i = 0 ; resetCount > i ; i ++) {
                pickedUpFootings.get(i).reset();
            }

        }

        renderSystem.render(Bukkit.getWorlds().get(0));
    }

    public static void giveEditItems(Player player) {

        player.getInventory().setItem(1, getNamedItemStack(Material.ARROW, "足場x3"));
        player.getInventory().setItem(2, getNamedItemStack(Material.ARROW, "足場x6"));
        player.getInventory().setItem(4, getNamedItemStack(Material.BARRIER, "オフハンドに持つと近くの足場が消えるよ"));
        player.getInventory().setItem(6, getNamedItemStack(Material.ARROW, "足場x12"));
        player.getInventory().setItem(7, getNamedItemStack(Material.ARROW, "足場x24"));
    }

    private static ItemStack getNamedItemStack(Material material, String name) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
