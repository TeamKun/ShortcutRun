package net.kunmc.lab.shortcutrun.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;

public class CommandTest {

    public static void register() {

        CommandAPICommand render = new CommandAPICommand("render")
                .executes((commandSender, objects) -> {
                    ShortcutRunPlugin.getInstance().getMainManager().debug();
                });

        new CommandAPICommand("test")
                .withPermission(CommandPermission.OP)
                .withSubcommand(render)
                .register();
    }

}
