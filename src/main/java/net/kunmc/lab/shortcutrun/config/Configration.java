package net.kunmc.lab.shortcutrun.config;

import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class Configration {

    public static ConfigItem<Integer> renderFootingMax = new ConfigItem<>("renderFootingMax", 50, integer -> integer >= 0, "プレイヤーの頭上の足場の最大値", "0 以上の整数");

    public static ConfigItem<Integer> renderFootingCompressRatio = new ConfigItem<>("renderFootingCompressRatio", 1, integer -> integer >= 1, "プレイヤーの頭上の足場の圧縮率", "1 以上の整数");

    public static ConfigItem<Boolean> resetOnFinish = new ConfigItem<>("resetOnFinish", true, "ゲーム終了時の自動リセット", "true or false");

    public static ConfigItem<Double> attackRadis = new ConfigItem<>("attackRadis", 1.0d, d -> d >= 0, "他プレイヤーへの攻撃範囲", "0.0 以上の数");

    public static ConfigItem<Integer> accelerationLevel = new ConfigItem<>("accelerationLevel", 0, integer -> integer >= 0 && 256 > integer, "足場設置時の加速レベル", "0 以上 255 以下の整数");

    public static ConfigItem<Integer> accelerationTick = new ConfigItem<>("accelerationTick", 60, integer -> integer >= 0, "足場設置時の加速時間(tick)", "0 以上の整数");

    public static ConfigItem<Integer> footingGainRate = new ConfigItem<>("footingGainRate", 1, integer -> integer >= 0, "足場回収時の倍率", "0 以上の整数");

    public static ConfigItem<Double> footingGainDistance = new ConfigItem<>("footingGainDistance", 1.5d, d -> d >= 0, "足場回収の範囲", "0 以上の数");

    public static ConfigItem<Integer> footingRestorePerTick = new ConfigItem<>("footingRestorePerTick", 5, integer -> integer >= 0, "1tickあたりの足場復活量", "0 以上の整数");

    public static ConfigItem<Integer> placeFootingBlock = new ConfigItem<>("placeFootingBlock", 2, integer -> integer >= 1, "足場設置の最大ブロック範囲(あんまいじんなくてもいい)", "1 以上の整数");

    public static ConfigItem<Double> placeFootingDistance = new ConfigItem<>("placeFootingDistance", 1.0d, d -> d >= 0, "足場設置の範囲", "0 以上の数");

    public static ConfigItem<Double> editRemoveFootingDistance = new ConfigItem<>("editRemoveFootingDistance", 3.0d, d -> d >= 0, "編集時の足場削除範囲", "0 以上の数");



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

    public void setToDefault() {

        getAllConfigItem().forEach(configItem -> configMap.compute(configItem, (item, o) -> configItem.getDefaultValue()));

    }

    public void load() {
        ShortcutRunPlugin.getInstance().reloadConfig();

        loadValue(renderFootingMax);
        loadValue(renderFootingCompressRatio);
        loadValue(resetOnFinish);
        loadValue(attackRadis);
        loadValue(accelerationLevel);
        loadValue(accelerationTick);
        loadValue(footingGainRate);
        loadValue(footingGainDistance);
        loadValue(footingRestorePerTick);
        loadValue(placeFootingBlock);
        loadValue(placeFootingDistance);
        loadValue(editRemoveFootingDistance);
    }

    public void save() {
        getAllConfigItem().forEach(configItem -> {
            Object value = configMap.get(configItem);
            ShortcutRunPlugin.getInstance().getConfig().set(configItem.getConfigKey(), value);
        });
        ShortcutRunPlugin.getInstance().saveConfig();
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
