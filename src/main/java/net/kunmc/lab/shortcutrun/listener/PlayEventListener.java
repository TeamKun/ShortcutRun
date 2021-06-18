package net.kunmc.lab.shortcutrun.listener;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.manager.MainManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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
        int footingCount = mainManager.getFooting(player);
        if (footingCount <= 0) {
            return;
        }

        Location to = e.getTo();
        Location from = e.getFrom();

        if (to.distance(from) == 0) {
            return;
        }

        // pickup

        // pickup end


        // attack
        if (player.isOnGround() && player.isSprinting() && player.getPotionEffect(PotionEffectType.SPEED) == null) {

        }
        // attack end

        if (to.getBlockY() - from.getBlockY() != 0) {
            return;
        }
        Block block = to.getWorld().getBlockAt(to.getBlockX(), to.getBlockY() - 1, to.getBlockZ());
        if (!block.getType().equals(Material.AIR)) {
            return;
        }

        mainManager.placeFooting(player, block);
        mainManager.setFooting(player, footingCount - 1);

        // shortcutrun.renderFooting(player);
        // player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, shortcutrun.getConfig().getInt("duration", 60), shortcutrun.getConfig().getInt("level", 0)));

    }

    private boolean isExcluded(Player player) {
        GameMode gameMode = player.getGameMode();
        return gameMode.equals(GameMode.SPECTATOR) || gameMode.equals(GameMode.CREATIVE);
    }
}
