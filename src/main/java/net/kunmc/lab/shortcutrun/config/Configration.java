package net.kunmc.lab.shortcutrun.config;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class Configration {

    public static ConfigItem<Integer> maxRenderFooting = new ConfigItem<>("maxRenderFooting", 50, integer -> integer >= 0, "プレイヤーの頭上の足場の最大値", "0 以上の整数");
    public static ConfigItem<Boolean> resetOnFinish = new ConfigItem<>("resetOnFinish", true, "ゲーム終了時の自動リセット", "true or false");

    private Map<ConfigItem, Object> configMap = new HashMap<>();

    public <T> T get(ConfigItem<T> configItem) {
        return (T) configMap.get(configItem);
    }

    public <T> void set(ConfigItem<T> configItem, Object value) {

        if (configItem.isValid(value)) {

            configMap.compute(configItem, (item, o) -> configItem.cast(value));

        } else {

            configMap.compute(configItem, (item, o) -> item.getDefaultValue());

        }
    }

    public void load() {
        loadValue(maxRenderFooting);
        loadValue(resetOnFinish);
    }

    private void loadValue(ConfigItem configItem) {
        set(configItem, ShortcutRunPlugin.getInstance().getConfig().get(configItem.getConfigKey()));
    }

    public Set<ConfigItem> getAllConfigItem() {
        return configMap.keySet();
    }

    public ConfigItem getConfigItem(String name) {
        List<ConfigItem> configItemSet = getAllConfigItem().stream()
                .filter(configItem -> configItem.getConfigKey().equals(name))
                .collect(Collectors.toList());
        return configItemSet.isEmpty() ? null : configItemSet.get(0);
    }

}
