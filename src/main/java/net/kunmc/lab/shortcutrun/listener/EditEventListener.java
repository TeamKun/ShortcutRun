package net.kunmc.lab.shortcutrun.listener;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.gameobject.Footing;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import net.kunmc.lab.shortcutrun.manager.MainManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.stream.Collectors;

public class EditEventListener implements Listener {

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {

        MainManager mainManager = ShortcutRunPlugin.getInstance().getMainManager();

        Footing clickedFooting = mainManager.getRenderSystem().getFootingByEntity(event.getRightClicked());

        if (clickedFooting == null) {
            return;
        }

        Stage stage = mainManager.getSelectedStage();

        if (stage != clickedFooting.parentStage) {
            return;
        }

        stage.footings = stage.footings.stream().filter(footing -> footing != clickedFooting).collect(Collectors.toList());
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {



    }
}
