package net.kunmc.lab.shortcutrun.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.gameobject.Footing;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class StageManager {

    private Map<String, Stage> stages = new HashMap<>();

    public void add(Stage stage) {
        stages.put(stage.getName(), stage);
    }

    public Stage findStageFromName(String name) {
        return stages.get(name);
    }

    public void load() {
        ShortcutRunPlugin pluginInstance = ShortcutRunPlugin.getInstance();
        FileConfiguration config = pluginInstance.getConfig();
        JsonElement jsonElement = new Gson().toJsonTree(config.get("stages"));

        JsonArray jsonArray;
        try {
            jsonArray = jsonElement.getAsJsonArray();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return;
        }

        Iterator<JsonElement> iteratorStages = jsonArray.iterator();
        while (iteratorStages.hasNext()) {
            JsonElement jsonFooting = iteratorStages.next();
            if (!(jsonFooting instanceof JsonObject)) {
                continue;
            }
            Stage stage = Stage.fromJsonObject((JsonObject) jsonFooting);
            add(stage);
        }
    }

    public void save() {

        JsonArray jsonArray = new JsonArray();
        getStages().forEach(stage -> {
            JsonObject jsonObject = stage.save();
            jsonArray.add(jsonObject);
        });

        ShortcutRunPlugin pluginInstance = ShortcutRunPlugin.getInstance();
        FileConfiguration config = pluginInstance.getConfig();
        Object object = new Gson().fromJson(jsonArray, new TypeToken<ArrayList>(){}.getType());
        config.set("stages", object);
        pluginInstance.saveConfig();

    }

    public List<Stage> getStages() {
        return stages.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
    }
}
