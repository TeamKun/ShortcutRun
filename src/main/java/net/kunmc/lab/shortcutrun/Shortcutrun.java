package net.kunmc.lab.shortcutrun;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class Shortcutrun extends JavaPlugin {

    private static final String SCOREBOARD_NAME = "footings";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
        new Command(this).register();

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective objective = scoreboard.getObjective(SCOREBOARD_NAME);
        if (objective == null) {
            scoreboard.registerNewObjective(SCOREBOARD_NAME, "", SCOREBOARD_NAME);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> removePassengers(player));
    }

    public void addFooting(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();
        getTopPassenger(player).addPassenger(world.spawnEntity(location, EntityType.SILVERFISH, CreatureSpawnEvent.SpawnReason.CUSTOM, entity1 -> {
            Silverfish silverfish = ((Silverfish) entity1);
            silverfish.setSilent(true);
            silverfish.setAI(false);
            silverfish.setInvulnerable(true);
            silverfish.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
            silverfish.addPassenger(world.spawnEntity(location, EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, entity2 -> {
                ArmorStand armorStand = ((ArmorStand) entity2);
                armorStand.setMarker(true);
                armorStand.setHelmet(new ItemStack(Material.OAK_SLAB));
                armorStand.setVisible(false);
                armorStand.getLocation().setYaw(0);
            }));
        }));
    }

    private Entity getTopPassenger(Entity entity) {
        if (entity.getPassengers().isEmpty()) {
            return entity;
        }
        return getTopPassenger(entity.getPassengers().get(0));
    }

    private int getPassengerCount(Entity entity) {
        if (entity.getPassengers().isEmpty()) {
            return 0;
        }
        return getPassengerCount(entity.getPassengers().get(0)) + 1;
    }

    private void setScoreboard(Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective(SCOREBOARD_NAME);
        if (objective == null) {
            scoreboard.registerNewObjective(SCOREBOARD_NAME, "", SCOREBOARD_NAME);
        }
    }

    public int getFooting(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        setScoreboard(scoreboard);
        return scoreboard.getObjective(SCOREBOARD_NAME).getScore(player.getName()).getScore();
    }

    public void setFooting(Player player, int value) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        setScoreboard(scoreboard);
        scoreboard.getObjective(SCOREBOARD_NAME).getScore(player.getName()).setScore(value);
    }

    public void renderFooting(Player player) {
        int footings = getFooting(player);
        int realFootings = getPassengerCount(player) / 2;
        int dif = footings - realFootings;
        if (dif == 0) {
            return;
        }
        if (dif > 0) {
            for (int i = 1 ; i <= dif ; i ++) {
                addFooting(player);
            }
        } else {
            removePassengers(getPassengerByNumber(player, footings * 2));
        }
    }

    public Entity getPassengerByNumber(Entity entity, int num) {
        if (num <= 0) {
            return entity;
        }
        if (entity.getPassengers().isEmpty()) {
            return entity;
        }
        return getPassengerByNumber(entity.getPassengers().get(0), num - 1);
    }

    public void removePassengers(Entity entity) {
        if (entity.getPassengers().isEmpty()) {
            return;
        }
        Entity passenger = entity.getPassengers().get(0);
        removePassengers(passenger);
        passenger.remove();
    }
}
