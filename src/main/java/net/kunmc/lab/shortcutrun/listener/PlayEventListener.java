package net.kunmc.lab.shortcutrun.listener;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.gameobject.Footing;
import net.kunmc.lab.shortcutrun.manager.MainManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PlayEventListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        ShortcutRunPlugin pluginInstance = ShortcutRunPlugin.getInstance();
        MainManager mainManager = pluginInstance.getMainManager();

        if (!mainManager.isPlaying()) {
            return;
        }

        Player player = e.getPlayer();

        if (isExcluded(player)) {
            return;
        }

        if (!player.isOnGround()) {
            return;
        }

        int footingAmount = mainManager.getFootingAmount(player);

        Location to = e.getTo();
        Location from = e.getFrom();

        if (to.distance(from) == 0) {
            return;
        }

        // 足場を拾う処理 begin

        List<Footing> footings = mainManager.getSelectedStage().getNearbyFooting(player.getLocation(), 1.5);

        footings.stream()
                .filter(footing -> !footing.isPickedUp())
                .forEach(footing -> {
                    footing.pickUp();
                    mainManager.setFootingAmount(player, footingAmount + 1);
                });

        // 足場を拾う処理 end

        if (footingAmount <= 0) {
            return;
        }

        // attack
        if (player.isOnGround() && player.isSprinting() && player.getPotionEffect(PotionEffectType.SPEED) == null) {

        }
        // attack end

        if (to.getBlockY() - from.getBlockY() != 0) {
            return;
        }

        boolean placed = mainManager.tryPlaceFooting(to.clone().add(0, -1, 0));
        if (!placed) {
            return;
        }
        mainManager.setFootingAmount(player, footingAmount - 1);

        // shortcutrun.renderFooting(player);
        // player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, shortcutrun.getConfig().getInt("duration", 60), shortcutrun.getConfig().getInt("level", 0)));

    }

    private boolean isExcluded(Player player) {
        GameMode gameMode = player.getGameMode();
        return gameMode.equals(GameMode.SPECTATOR) || gameMode.equals(GameMode.CREATIVE);
    }
}
