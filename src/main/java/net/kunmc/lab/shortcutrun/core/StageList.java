package net.kunmc.lab.shortcutrun.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.exception.ConfigLoadException;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class StageList {

    private List<Stage> stages = new ArrayList<>();

    public void load() throws ConfigLoadException {
        ShortcutRunPlugin pluginInstance = ShortcutRunPlugin.getInstance();
        FileConfiguration config = pluginInstance.getConfig();
        JsonElement jsonElement = new Gson().toJsonTree(config.get("stages"));

        JsonArray jsonArray;
        try {
            jsonArray = jsonElement.getAsJsonArray();
        } catch (IllegalStateException e) {
            throw new ConfigLoadException("configファイルの読み込みに失敗しました！");
        }

        Iterator<JsonElement> iteratorStages = jsonArray.iterator();
        while (iteratorStages.hasNext()) {
            JsonElement jsonFooting = iteratorStages.next();
            if (!(jsonFooting instanceof JsonObject)) {
                continue;
            }
            stages.add(Stage.fromJsonObject((JsonObject) jsonFooting));
        }
    }

    public void add(Stage stage) {
        stages.add(stage);
    }

    public Stage findStageFromName(String name) {
        List<Stage> matchedStage = stages.stream().filter(stage -> stage.getName().equals(name)).collect(Collectors.toList());
        if (matchedStage.isEmpty()) {
            return null;
        }
        return matchedStage.get(0);
    }
}
