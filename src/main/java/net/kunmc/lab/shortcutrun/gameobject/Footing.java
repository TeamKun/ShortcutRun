package net.kunmc.lab.shortcutrun.gameobject;

import com.google.gson.JsonObject;
import org.bukkit.util.Vector;

public class Footing {

    private Vector location;

    private boolean isPickedUp = false;

    public static Footing fromJsonObject(JsonObject jsonObject) {
        Footing footing = new Footing();
        footing.load(jsonObject);
        return footing;
    }

    public void load(JsonObject jsonObject) {

        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        location = new Vector(x, y, z);

    }

    public JsonObject save() {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("x", location.getX());
        jsonObject.addProperty("y", location.getY());
        jsonObject.addProperty("z", location.getZ());

        return jsonObject;
    }

    public Vector getLocation() {
        return location;
    }
}
