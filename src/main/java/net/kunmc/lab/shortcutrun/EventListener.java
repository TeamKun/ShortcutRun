package net.kunmc.lab.shortcutrun;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class EventListener implements Listener {

    private final Shortcutrun shortcutrun;

    public EventListener(Shortcutrun shortcutrun) {
        this.shortcutrun = shortcutrun;
    }

    @EventHandler
    public void onMove2(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (isExcluded(player)) {
            return;
        }
        if (!player.isOnGround()) {
            return;
        }
        if (!player.isSprinting()) {
            return;
        }
        if (player.getPotionEffect(PotionEffectType.SPEED) == null) {
            return;
        }
        Vector to = e.getTo().toVector();
        Vector from = e.getFrom().toVector();
        if (to.distance(from) == 0) {
            return;
        }
        Bukkit.getOnlinePlayers().stream().filter(otherPLayer -> !otherPLayer.equals(player)).forEach(otherPlayer -> {
            if (isExcluded(otherPlayer)) {
                return;
            }
            if (!player.isOnGround()) {
                return;
            }
            if (otherPlayer.getLocation().distance(player.getLocation()) < 1.0) {
                otherPlayer.setVelocity(new Vector(0, 2, 0));
            }
        });
    }

    @EventHandler
    public void onMove1(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (isExcluded(player)) {
            return;
        }
        if (!player.isOnGround()) {
            return;
        }
        Location to = e.getTo();
        Location from = e.getFrom();
        if (to.getBlockY() - from.getBlockY() != 0) {
            return;
        }
        Block block = to.getWorld().getBlockAt(to.getBlockX(), to.getBlockY() - 1, to.getBlockZ());
        if (!block.getType().equals(Material.AIR)) {
            return;
        }
        int footings = shortcutrun.getFooting(player);
        if (footings <= 0) {
            return;
        }
        shortcutrun.setFooting(player, footings - 1);
        shortcutrun.renderFooting(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 0));
        block.setType(Material.OAK_PLANKS);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (!(e.getDismounted() instanceof Player)) {
            return;
        }
        shortcutrun.removePassengers(e.getEntity());
        e.getEntity().remove();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        shortcutrun.setFooting(e.getEntity(), 0);
    }

    private boolean isExcluded(Player player) {
        GameMode gameMode = player.getGameMode();
        return gameMode.equals(GameMode.SPECTATOR) || gameMode.equals(GameMode.CREATIVE);
    }
}
