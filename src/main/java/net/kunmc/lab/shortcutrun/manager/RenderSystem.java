package net.kunmc.lab.shortcutrun.manager;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.gameobject.Footing;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;
import java.util.stream.Collectors;

public class RenderSystem {

    private Stage stage;
    private Map<Footing, Entity> footingEntities = new HashMap<>();

    public void setStage(Stage stage) {
        this.stage = stage;
        clear();
    }

    public void clear() {
        footingEntities.forEach((footing, entity) -> entity.remove());
        footingEntities.clear();
    }

    public void render(World world) {

        if (stage == null) {
            return;
        }

        Iterator<Map.Entry<Footing, Entity>> iterator = footingEntities.entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<Footing, Entity> entry = iterator.next();
            Footing footing = entry.getKey();
            Entity entity = entry.getValue();

            // 削除された足場をMapから除外
            if (footing.isRemoved()) {
                iterator.remove();
                entity.remove();
                continue;
            }

            // プレイ中、拾われた足場をMapから除外
            if (footing.isPickedUp() && ShortcutRunPlugin.getInstance().getMainManager().isPlaying()) {
                iterator.remove();
                entity.remove();
                continue;
            }

            // 足場に対応するEntityがkillされた場合、再表示のためMapから除外
            if (entity.isDead()) {
                iterator.remove();
                continue;
            }
        }


        // 足場を表示
        // 拾われた足場は表示しないようにフィルタリング

        stage.footings.stream()
                .filter(footing -> !footing.isPickedUp())
                .forEach(footing -> {

                    Entity entity = footingEntities.get(footing);

                    if (entity != null) {
                        return;
                    }

                    Entity newEntity = RenderSystem.Utils.createFooting(footing.getAsBukkitLocation(world).add(0, -0.6, 0));

                    footingEntities.put(footing, newEntity);

                }
        );
    }

    public void addFooting(Player player) {

        Location location = player.getLocation();

        Silverfish silverfish = Utils.spwanConnectiveSilverfish(location);
        ArmorStand armorStand = Utils.spwanFootingArmorStand(location);
        silverfish.addPassenger(armorStand);

        Utils.getTopPassenger(player).addPassenger(silverfish);

    }

    public Entity getEntityByFooting(Footing footing) {
        return footingEntities.get(footing);
    }

    public Footing getFootingByEntity(Entity entity) {
        List<Footing> matchedFootings = footingEntities
                .entrySet()
                .stream()
                .filter(entry -> entity == entry.getValue())
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());
        return matchedFootings.isEmpty() ? null : matchedFootings.get(0);
    }

    public void renderFooting(Player player) {
        int footings = 0;
        int realFootings = Utils.getPassengerCount(player) / 2;
        int dif = footings - realFootings;


        if (dif == 0) {
            return;
        }
        int max = 50;
        if (dif > 0) {
            dif = Math.min(dif, max);
            for (int i = 1 ; i <= dif ; i ++) {
                addFooting(player);
            }
        } else {
            Utils.removePassengers(Utils.getPassengerByNumber(player, footings * 2));
        }
    }

    private static class Utils {

        private static Silverfish spwanConnectiveSilverfish(Location location) {
            return (Silverfish) location.getWorld().spawnEntity(
                    location,
                    EntityType.SILVERFISH,
                    CreatureSpawnEvent.SpawnReason.CUSTOM,
                    entity -> {
                        Silverfish silverfish = ((Silverfish) entity);
                        silverfish.setSilent(true);
                        silverfish.setAI(false);
                        silverfish.setInvulnerable(true);
                        silverfish.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                    }
            );
        }

        private static ArmorStand spwanFootingArmorStand(Location location) {
            return (ArmorStand) location.getWorld().spawnEntity(
                    location,
                    EntityType.ARMOR_STAND,
                    CreatureSpawnEvent.SpawnReason.CUSTOM,
                    entity -> {
                        ArmorStand armorStand = ((ArmorStand) entity);
                        armorStand.setMarker(true);
                        armorStand.setHelmet(new ItemStack(Material.OAK_SLAB));
                        armorStand.setVisible(false);
                    }
            );
        }

        private static ArmorStand createFooting(Location location) {
            return (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
                ArmorStand armorStand = ((ArmorStand) entity);
                armorStand.setMarker(true);
                armorStand.setSmall(true);
                armorStand.setGlowing(true);
                armorStand.setHelmet(new ItemStack(Material.OAK_SLAB));
                armorStand.setVisible(false);
            });
        }

        private static Entity getTopPassenger(Entity entity) {
            if (entity.getPassengers().isEmpty()) {
                return entity;
            }
            return getTopPassenger(entity.getPassengers().get(0));
        }

        private static Entity getPassengerByNumber(Entity entity, int num) {
            if (num <= 0) {
                return entity;
            }
            if (entity.getPassengers().isEmpty()) {
                return entity;
            }
            return getPassengerByNumber(entity.getPassengers().get(0), num - 1);
        }

        private static void removePassengers(Entity entity) {
            if (entity.getPassengers().isEmpty()) {
                return;
            }
            Entity passenger = entity.getPassengers().get(0);
            removePassengers(passenger);
            passenger.remove();
        }

        private static int getPassengerCount(Entity entity) {
            if (entity.getPassengers().isEmpty()) {
                return 0;
            }
            return getPassengerCount(entity.getPassengers().get(0)) + 1;
        }
    }
}
