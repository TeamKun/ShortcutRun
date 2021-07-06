package net.kunmc.lab.shortcutrun.listener;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.config.Configration;
import net.kunmc.lab.shortcutrun.gameobject.Footing;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import net.kunmc.lab.shortcutrun.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditEventListener implements Listener {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {

        MainManager mainManager = ShortcutRunPlugin.getInstance().getMainManager();

        if (!mainManager.isEditing()) {
            return;
        }

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        if (event.getItem() == null) {
            return;
        }

        if (!event.getItem().getType().equals(Material.ARROW)) {
            return;
        }

        Stage stage = mainManager.getSelectedStage();

        if (stage == null) {
            return;
        }

        Location interactionPoint = event.getInteractionPoint();

        if (interactionPoint == null) {
            return;
        }

        Player player = event.getPlayer();

        // 足場追加処理 begin

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(interactionPoint.getWorld(), 0, 0.20, 0));
        locations.add(new Location(interactionPoint.getWorld(), 0.20, 0, 0));
        locations.add(new Location(interactionPoint.getWorld(), -0.20, 0, 0));

        switch (event.getItem().getItemMeta().getDisplayName()) {

            case "足場x24":
                new ArrayList<>(locations).forEach(location -> {
                    locations.add(location.clone().add(0, 0, 0.8));
                });
            case "足場x12":
                new ArrayList<>(locations).forEach(location -> {
                    locations.add(location.clone().add(0, 0, 0.4));
                });
            case "足場x6":
                new ArrayList<>(locations).forEach(location -> {
                    locations.add(location.clone().add(0, 0.4, 0));
                });
            case "足場x3":
            default:
                break;
        }

        locations.forEach(location -> {
            Vector offsetVec = new Vector(0, 0, 1)
                    .multiply(player.getLocation().distance(interactionPoint))
                    .add(location.toVector())
                    .rotateAroundY(Math.toRadians(- player.getLocation().getYaw()));
            location = player.getLocation().clone();
            location.setY(interactionPoint.getY());
            location.add(offsetVec);
            Footing footing = new Footing(location.getX(), location.getY(), location.getZ(), player.getLocation().getYaw(), 0);
            stage.addFooting(footing);
        });

        event.setCancelled(true);

        // 足場追加処理 end

    }

    // 削除処理

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        ShortcutRunPlugin pluginInstance = ShortcutRunPlugin.getInstance();
        MainManager mainManager = pluginInstance.getMainManager();

        if (!mainManager.isEditing()) {
            return;
        }

        Stage stage = mainManager.getSelectedStage();

        if (stage == null) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.getInventory().getItemInOffHand().getType().equals(Material.BARRIER)) {
            return;
        }

        stage.getNearbyFooting(player.getLocation(), Configration.editRemoveFootingDistance.get()).forEach(footing -> footing.remove());
    }
}
