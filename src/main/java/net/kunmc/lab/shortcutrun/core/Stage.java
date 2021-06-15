package net.kunmc.lab.shortcutrun.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Stage {

    private String name;
    private List<Footing> footings = new ArrayList<>();

    public static Stage fromJsonObject(JsonObject jsonObject) {
        Stage stage = new Stage();
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
            footings.add(Footing.fromJsonObject((JsonObject) jsonFooting));
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

    public String getName() {
        return name;
    }
}
