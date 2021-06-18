package net.kunmc.lab.shortcutrun.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.command.argument.StageArgument;
import net.kunmc.lab.shortcutrun.gameobject.Stage;

public class Command {

    public static void register() {

        CommandAPICommand edit = new CommandAPICommand("edit")

                .withSubcommand(new CommandAPICommand("create")
                        .withArguments(new StringArgument("stage_name"))
                        .executes((commandSender, objects) -> {
                            ShortcutRunPlugin
                                    .getInstance()
                                    .getStageManager()
                                    .add(new Stage((String) objects[0]));
                            commandSender.sendMessage("ステージを追加しました");
                        })
                );

        CommandAPICommand select = new CommandAPICommand("select")

                .withArguments(new StageArgument("stage"))
                .executes((commandSender, objects) -> {
                    ShortcutRunPlugin
                            .getInstance()
                            .getMainManager()
                            .setStage((Stage) objects[0]);
                    commandSender.sendMessage("ステージを選択しました");
                });

        CommandAPICommand play = new CommandAPICommand("play");

        CommandAPICommand config = new CommandAPICommand("config")

                .withSubcommand(new CommandAPICommand("save")
                        .executes((commandSender, objects) -> {
                            ShortcutRunPlugin
                                    .getInstance()
                                    .getStageManager()
                                    .save();
                            commandSender.sendMessage("configを保存しました");
                        })
                )

                .withSubcommand(new CommandAPICommand("load")
                        .executes((commandSender, objects) -> {
                            ShortcutRunPlugin
                                    .getInstance()
                                    .getStageManager()
                                    .load();
                            commandSender.sendMessage("configを保存しました");
                        })
                );


        new CommandAPICommand("shortcutrun")
                .withPermission(CommandPermission.OP)
                .withSubcommand(edit)
                .withSubcommand(select)
                .withSubcommand(config)
                .register();
    }

}
