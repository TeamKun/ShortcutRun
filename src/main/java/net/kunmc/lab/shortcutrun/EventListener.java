package net.kunmc.lab.shortcutrun;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.List;

public class EventListener implements Listener {

    private final Shortcutrun shortcutrun;

    public EventListener(Shortcutrun shortcutrun) {
        this.shortcutrun = shortcutrun;
    }

    @EventHandler
    public void pickupFooting(PlayerMoveEvent e) {
        if (!shortcutrun.isActive()) {
            return;
        }
        Player player = e.getPlayer();
        if (isExcluded(player)) {
            return;
        }
        Vector to = e.getTo().toVector();
        Vector from = e.getFrom().toVector();
        if (to.distance(from) == 0) {
            return;
        }
        player.getWorld().getEntities().forEach(entity -> {
            if (!(entity instanceof ArmorStand)) {
                return;
            }
            ArmorStand armorStand = (ArmorStand) entity;
            if (!(armorStand.getItem(EquipmentSlot.HEAD).getType().equals(Material.OAK_SLAB))) {
                return;
            }
            if (!armorStand.isSmall()) {
                return;
            }
            if (armorStand.getLocation().distance(player.getLocation()) > shortcutrun.getConfig().getDouble("distance", 1)) {
                return;
            }
            armorStand.remove();
            shortcutrun.setFooting(player, shortcutrun.getFooting(player) + 1);
            shortcutrun.renderFooting(player);
        });
    }

    @EventHandler
    public void attack(PlayerMoveEvent e) {
        if (!shortcutrun.isActive()) {
            return;
        }
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
            if (otherPlayer.getLocation().distance(player.getLocation()) < shortcutrun.getConfig().getDouble("distance", 1)) {
                otherPlayer.setVelocity(new Vector(0, shortcutrun.getConfig().getDouble("velocity", 1), 0));
            }
        });
    }

    @EventHandler
    public void placeFooting(PlayerMoveEvent e) {
        if (!shortcutrun.isActive()) {
            return;
        }
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, shortcutrun.getConfig().getInt("duration", 60), shortcutrun.getConfig().getInt("level", 0)));
        block.setType(Material.OAK_PLANKS);
        shortcutrun.addLocation(block.getLocation());
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
        if (shortcutrun.getConfig().getBoolean("reset", true))
        shortcutrun.setFooting(e.getEntity(), 0);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Silverfish)) {
            return;
        }
        Silverfish silverfish = (Silverfish) e.getEntity();
        if (silverfish.isInvulnerable()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void setFooting(PlayerInteractEvent e) {
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        ItemStack itemStack = e.getPlayer().getItemInHand();
        if (!itemStack.getType().equals(Material.OAK_SLAB)) {
            return;
        }
        List<Location> locations = new ArrayList<>();
        Location blockPos = e.getClickedBlock().getLocation().clone();
        switch (itemStack.getItemMeta().getDisplayName()) {
            case "シングル":
                locations.add(new Location(blockPos.getWorld(), blockPos.getBlockX() + 0.5, blockPos.getBlockY() + 1, blockPos.getBlockZ() + 0.5));
                break;
            case "トリプル1":
                locations.add(new Location(blockPos.getWorld(), blockPos.getBlockX() + 0.5, blockPos.getBlockY() + 1.2, blockPos.getBlockZ() + 0.5));
                locations.add(new Location(blockPos.getWorld(), blockPos.getBlockX() + 0.3, blockPos.getBlockY() + 1, blockPos.getBlockZ() + 0.5));
                locations.add(new Location(blockPos.getWorld(), blockPos.getBlockX() + 0.7, blockPos.getBlockY() + 1, blockPos.getBlockZ() + 0.5));
                break;
            case "トリプル2":
                locations.add(new Location(blockPos.getWorld(), blockPos.getBlockX() + 0.5, blockPos.getBlockY() + 1.2, blockPos.getBlockZ() + 0.5));
                locations.add(new Location(blockPos.getWorld(), blockPos.getBlockX() + 0.5, blockPos.getBlockY() + 1, blockPos.getBlockZ() + 0.3));
                locations.add(new Location(blockPos.getWorld(), blockPos.getBlockX() + 0.5, blockPos.getBlockY() + 1, blockPos.getBlockZ() + 0.7));
                break;
            default:
                break;
        }
        if (locations.isEmpty()) {
            return;
        }
        e.setCancelled(true);
        locations.forEach(location -> createFooting(location));
    }

    private ArmorStand createFooting(Location location) {
        return (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
            ArmorStand armorStand = ((ArmorStand) entity);
            armorStand.setMarker(true);
            armorStand.setSmall(true);
            armorStand.setGlowing(true);
            armorStand.setHelmet(new ItemStack(Material.OAK_SLAB));
            armorStand.setVisible(false);
        });
    }

    private boolean isExcluded(Player player) {
        GameMode gameMode = player.getGameMode();
        return gameMode.equals(GameMode.SPECTATOR) || gameMode.equals(GameMode.CREATIVE);
    }
}
