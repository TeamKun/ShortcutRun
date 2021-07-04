package net.kunmc.lab.shortcutrun.listener;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.gameobject.Footing;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import net.kunmc.lab.shortcutrun.manager.MainManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditEventListener implements Listener {

    @EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent event) {

        MainManager mainManager = ShortcutRunPlugin.getInstance().getMainManager();

        Entity clickedEntity = event.getRightClicked();

        Footing clickedFooting = mainManager.getRenderSystem().getFootingByEntity(clickedEntity);

        if (clickedFooting == null) {
            return;
        }

        Stage stage = mainManager.getSelectedStage();

        if (stage != clickedFooting.parentStage) {
            return;
        }

        stage.footings = stage.footings.stream().filter(footing -> footing != clickedFooting).collect(Collectors.toList());
        clickedEntity.remove();

        event.setCancelled(true);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!event.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        MainManager mainManager = ShortcutRunPlugin.getInstance().getMainManager();

        Stage stage = mainManager.getSelectedStage();

        if (stage == null) {
            return;
        }

        Location location = event.getInteractionPoint();

        if (location == null) {
            return;
        }

        Player player = event.getPlayer();

        Footing footing = new Footing(location.getX(), location.getY(), location.getZ(), player.getLocation().getYaw(), 0);
        stage.addFooting(footing);

        mainManager.getRenderSystem().render(event.getPlayer().getWorld());

        event.setCancelled(true);

    }
}
