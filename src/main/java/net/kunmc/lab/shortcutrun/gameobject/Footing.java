package net.kunmc.lab.shortcutrun.gameobject;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.stream.Collectors;

public class Footing {

    public Stage parentStage;

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    private boolean isPickedUp = false;

    public Footing(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    private Footing() {}

    public static Footing fromJsonObject(JsonObject jsonObject) {
        Footing footing = new Footing();
        footing.load(jsonObject);
        return footing;
    }

    public void remove() {
        if (isRemoved()) {
            return;
        }
        parentStage.footings.remove(this);
        parentStage = null;
    }

    public boolean isRemoved() {
        return parentStage == null;
    }

    public boolean isPickedUp() {
        return isPickedUp;
    }

    public void pickUp() {
        isPickedUp = true;
    }

    public void reset() {
        isPickedUp = false;
    }

    public void load(JsonObject jsonObject) {

        x = jsonObject.get("x").getAsDouble();
        y = jsonObject.get("y").getAsDouble();
        z = jsonObject.get("z").getAsDouble();
        yaw = jsonObject.get("yaw").getAsFloat();
        pitch = jsonObject.get("pitch").getAsFloat();

    }

    public JsonObject save() {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("x", x);
        jsonObject.addProperty("y", y);
        jsonObject.addProperty("z", z);
        jsonObject.addProperty("yaw", yaw);
        jsonObject.addProperty("pitch", pitch);

        return jsonObject;
    }

    public Location getAsBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

}
