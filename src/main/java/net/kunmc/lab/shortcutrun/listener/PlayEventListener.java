package net.kunmc.lab.shortcutrun.listener;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.config.Configration;
import net.kunmc.lab.shortcutrun.gameobject.Footing;
import net.kunmc.lab.shortcutrun.manager.MainManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
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
                    mainManager.setFootingAmount(player, footingAmount + Configration.footingGainRate.get());
                });

        // 足場を拾う処理 end

        if (footingAmount <= 0) {
            return;
        }

        // player への攻撃処理 begin
        if (player.isOnGround() && player.isSprinting() && player.getPotionEffect(PotionEffectType.SPEED) != null) {

            double distance = Configration.attackRadis.get();

            Bukkit.getOnlinePlayers().stream()
                    .forEach(otherPlayer -> {

                        if (otherPlayer == player) return;

                        if (isExcluded(otherPlayer)) return;

                        if (otherPlayer.isDead()) return;

                        if (!player.getWorld().equals(otherPlayer.getWorld())) return;

                        if (player.getLocation().distance(otherPlayer.getLocation()) > distance) return;

                        otherPlayer.setHealth(0);
                        otherPlayer.getWorld().createExplosion(otherPlayer.getLocation(), 4, false, false);
                    });

        }
        // player への攻撃処理 end

        if (to.getBlockY() - from.getBlockY() != 0) {
            return;
        }

        // 足場の設置処理 begin

        boolean placed = mainManager.tryPlaceFooting(to.clone().add(0, -1, 0));
        if (!placed) {
            return;
        }
        mainManager.setFootingAmount(player, footingAmount - 1);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Configration.accelerationTick.get(), Configration.accelerationLevel.get()));

        // 足場の設置処理 end

    }

    private boolean isExcluded(Player player) {
        GameMode gameMode = player.getGameMode();
        return gameMode.equals(GameMode.SPECTATOR) || gameMode.equals(GameMode.CREATIVE);
    }
}
