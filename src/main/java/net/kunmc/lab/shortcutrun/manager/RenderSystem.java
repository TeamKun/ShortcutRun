package net.kunmc.lab.shortcutrun.manager;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.config.Configration;
import net.kunmc.lab.shortcutrun.gameobject.Footing;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

        // falling Blockが消えないようにする
        Bukkit.getWorlds().get(0).getEntitiesByClass(FallingBlock.class).stream()
                .filter(fallingBlock -> fallingBlock.getBlockData().getMaterial().equals(Material.OAK_PLANKS) || fallingBlock.getBlockData().getMaterial().equals(Material.BARRIER))
                .forEach(fallingBlock -> {

                    if (Utils.getBottomVehicle(fallingBlock) instanceof Player) {

                        fallingBlock.setTicksLived(1);

                    } else {

                        fallingBlock.remove();

                    }

                });

        // player の頭上の足場の表示
        Bukkit.getOnlinePlayers().forEach(player -> {
            renderPlayerFooting(player);
        });

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

    public void renderPlayerFooting(Player player) {

        int footingAmount = ShortcutRunPlugin.getInstance().getMainManager().getFootingAmount(player);

        if (footingAmount <= 0) {

            player.getPassengers().forEach(entity -> entity.remove());
            return;

        }

        if (player.getPassengers().isEmpty()) {

            player.addPassenger(Utils.spwanFootingFallingBlock(player.getLocation(), Material.BARRIER));

        }

        if (player.isDead()) {
            return;
        }

        int maxRenderingBlock = Configration.renderFootingMax.get();
        int compressRatio = Configration.renderFootingCompressRatio.get();

        int now = Utils.getPassengerNumber(player.getPassengers().get(0));
        int expected = Math.min(footingAmount / compressRatio, maxRenderingBlock);

        if (now == expected) {
            return;
        }

        if (now > expected) {

            for (int i = 1 ; i <= now - expected ; i ++) {
                Utils.getTopPassenger(player).remove();
            }

        } else {

            for (int i = 1 ; i <= expected - now ; i ++) {
                Utils.getTopPassenger(player).addPassenger(Utils.spwanFootingFallingBlock(player.getLocation(), Material.OAK_PLANKS));
            }

        }
    }

    private static class Utils {

        private static FallingBlock spwanFootingFallingBlock(Location location, Material material) {
            FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, material, (byte) 0);
            fallingBlock.setPersistent(true);
            fallingBlock.setInvulnerable(true);
            fallingBlock.setGravity(false);
            fallingBlock.setDropItem(false);
            fallingBlock.setSilent(true);
            return fallingBlock;
        }

        private static ArmorStand createFooting(Location location) {
            return (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
                ArmorStand armorStand = ((ArmorStand) entity);
                armorStand.setMarker(true);
                armorStand.setSmall(true);
                armorStand.setGlowing(true);
                armorStand.setHelmet(new ItemStack(Material.OAK_SLAB));
                armorStand.setVisible(false);
                armorStand.setSilent(true);
            });
        }

        private static Entity getTopPassenger(Entity entity) {
            if (entity.getPassengers().isEmpty()) {
                return entity;
            }
            return getTopPassenger(entity.getPassengers().get(0));
        }

        private static Entity getBottomVehicle(Entity entity) {
            Entity vehicle = entity.getVehicle();
            if (vehicle == null) {
                return entity;
            }
            return getBottomVehicle(vehicle);
        }

        // 再帰処理でエンティティの頭上のエンティティ数をカウント

        private static int getPassengerNumber(Entity entity) {
            if (entity.getPassengers().isEmpty()) {
                return 0;
            }
            return getPassengerNumber(entity.getPassengers().get(0)) + 1;
        }
    }
}
