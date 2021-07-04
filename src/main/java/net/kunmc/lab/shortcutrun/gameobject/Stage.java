package net.kunmc.lab.shortcutrun.gameobject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Stage {

    public String name;
    public List<Footing> footings = new ArrayList<>();

    public Stage(String name) {
        this.name = name;
    }

    public static Stage fromJsonObject(JsonObject jsonObject) {
        Stage stage = new Stage("");
        stage.load(jsonObject);
        return stage;
    }

    public void load(JsonObject jsonObject) {

        name = jsonObject.get("name").getAsString();

        Iterator<JsonElement> iteratorFooting = jsonObject.get("footings").getAsJsonArray().iterator();
        while (iteratorFooting.hasNext()) {
            JsonElement jsonFooting = iteratorFooting.next();
            if (!(jsonFooting instanceof JsonObject)) {
                continue;
            }
            Footing footing = Footing.fromJsonObject((JsonObject) jsonFooting);
            addFooting(footing);
        }

    }

    public JsonObject save() {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("name", name);

        JsonArray jsonArray = new JsonArray();
        Iterator<Footing> iteratorFooting = footings.stream().iterator();
        while (iteratorFooting.hasNext()) {
            jsonArray.add(iteratorFooting.next().save());
        }
        jsonObject.add("footings", jsonArray);

        return jsonObject;
    }

    public void addFooting(Footing footing) {
        footing.parentStage = this;
        footings.add(footing);
    }
}
