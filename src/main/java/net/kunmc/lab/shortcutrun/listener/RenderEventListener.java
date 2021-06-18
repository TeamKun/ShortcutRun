package net.kunmc.lab.shortcutrun.listener;

import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

public class RenderEventListener implements Listener {

    @EventHandler
    public void onDismount(EntityDismountEvent e) {
        if (!(e.getDismounted() instanceof Player)) {
            return;
        }
        // shortcutrun.removePassengers(e.getEntity());
        e.getEntity().remove();
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

}
