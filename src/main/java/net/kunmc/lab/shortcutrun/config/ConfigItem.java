package net.kunmc.lab.shortcutrun.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;

import java.lang.reflect.Type;
import java.util.function.Function;

public class ConfigItem<T> {

    private final T defaultValue;
    private final String configKey;
    private final Function<T, Boolean> validationFunction;
    private final String description;
    private final String condition;

    public ConfigItem(String configKey, T defaultValue, Function<T, Boolean> validationFunction, String description, String condition) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
        this.validationFunction = validationFunction;
        this.description = description;
        this.condition = condition;
    }

    public ConfigItem(String configKey, T defaultValue, String description, String condition) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
        this.validationFunction = o -> true;
        this.description = description;
        this.condition = condition;
    }

    public boolean isValid(Object object) {

        T castedObject = cast(object);

        if (castedObject == null) {
            return false;
        }

        return validationFunction.apply(castedObject);
    }

    public T cast(Object object) {

        try {
            return new Gson().fromJson(new Gson().toJsonTree(object), (Type) defaultValue.getClass());
        } catch (JsonSyntaxException e) {
            return null;
        }

    }
    
    public T getDefaultValue() {
        return defaultValue;
    }
    
    public String getConfigKey() {
        return configKey;
    }

    public String getDescription() {
        return description;
    }

    public String getCondition() {
        return condition;
    }

    public T get() {
        return ShortcutRunPlugin.getInstance().getConfigration().get(this);
    }
}
