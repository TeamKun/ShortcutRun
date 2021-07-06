package net.kunmc.lab.shortcutrun.command.argument;

import dev.jorel.commandapi.arguments.CustomArgument;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.config.ConfigItem;

public class ConfigItemArgument extends CustomArgument<ConfigItem> {

    public ConfigItemArgument(String nodeName) {
        super(nodeName, (input) -> {
            ConfigItem configItem = ShortcutRunPlugin.getInstance().getConfigration().getConfigItem(input);

            if(configItem == null) {
                throw new CustomArgumentException(new MessageBuilder("存在しない設定項目です: ").appendArgInput());
            }
            return configItem;
        });
        overrideSuggestions(sender ->
                ShortcutRunPlugin.getInstance().getConfigration().getAllConfigItem().stream()
                        .map(configItem -> configItem.getConfigKey())
                        .toArray(String[]::new)
        );
    }

}
