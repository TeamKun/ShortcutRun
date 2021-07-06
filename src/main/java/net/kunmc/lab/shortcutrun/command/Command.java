package net.kunmc.lab.shortcutrun.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EnchantmentArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.command.argument.ConfigItemArgument;
import net.kunmc.lab.shortcutrun.command.argument.StageArgument;
import net.kunmc.lab.shortcutrun.config.ConfigItem;
import net.kunmc.lab.shortcutrun.config.Configration;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import net.kunmc.lab.shortcutrun.manager.MainManager;
import net.kunmc.lab.shortcutrun.manager.StageManager;
import org.bukkit.ChatColor;

public class Command {

    public static void register() {

        CommandAPICommand edit = new CommandAPICommand("edit")

                .withSubcommand(new CommandAPICommand("create")
                        .withArguments(new StringArgument("stage_name"))
                        .executes((commandSender, objects) -> {
                            String stageName = (String) objects[0];
                            StageManager stageManager = ShortcutRunPlugin.getInstance().getStageManager();
                            Stage stage = stageManager.findStageFromName(stageName);
                            if (stage != null) {
                                commandSender.sendMessage(ChatColor.RED + "ステージ名:" + stageName + "は既に存在します！");
                                return;
                            }
                            stageManager.add(new Stage(stageName));
                            commandSender.sendMessage(ChatColor.GREEN + "ステージ名:" + stageName + "を新規作成しました");
                        })
                )

                .withSubcommand(new CommandAPICommand("delete")
                        .withArguments(new StageArgument("stage"))
                        .executes((commandSender, objects) -> {
                            MainManager mainManager = ShortcutRunPlugin.getInstance().getMainManager();
                            if (mainManager.isPlaying()) {
                                commandSender.sendMessage(ChatColor.RED + "プレイ中にこのコマンドは実行できません！");
                                return;
                            }
                            StageManager stageManager = ShortcutRunPlugin.getInstance().getStageManager();
                            Stage stage = (Stage) objects[0];
                            stageManager.delete(stage);
                            ShortcutRunPlugin.getInstance().getMainManager().unselect();
                            commandSender.sendMessage(ChatColor.GREEN + "ステージ名:" + stage.name + "を削除しました");
                        })
                )

                .withSubcommand(new CommandAPICommand("on")
                        .executes((commandSender, objects) -> {
                            MainManager mainManager = ShortcutRunPlugin.getInstance().getMainManager();
                            if (mainManager.isPlaying()) {
                                commandSender.sendMessage(ChatColor.RED + "プレイ中に編集はできません！");
                                return;
                            } else if (mainManager.isEditing()) {
                                commandSender.sendMessage(ChatColor.RED + "現在既に編集中です！");
                                return;
                            }
                            mainManager.setEditing(true);
                            commandSender.sendMessage(ChatColor.GREEN + "編集モードを有効化しました");
                            return;

                        })
                )

                .withSubcommand(new CommandAPICommand("off")
                        .executes((commandSender, objects) -> {
                            MainManager mainManager = ShortcutRunPlugin.getInstance().getMainManager();
                            if (!mainManager.isEditing()) {
                                commandSender.sendMessage(ChatColor.RED + "現在は編集中ではありません！");
                                return;
                            }
                            mainManager.setEditing(false);
                            ShortcutRunPlugin.getInstance().getStageManager().save();
                            commandSender.sendMessage(ChatColor.GREEN + "編集モードを無効化しました");
                            return;

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

        CommandAPICommand stageInfo = new CommandAPICommand("stageInfo")

                .executes((commandSender, objects) -> {
                    Stage stage = ShortcutRunPlugin
                            .getInstance()
                            .getMainManager()
                            .getSelectedStage();
                    if (stage == null) {
                        commandSender.sendMessage(ChatColor.RED + "ステージが選択されていません！");
                        return;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder
                            .append("ステージ情報").append("\n")
                            .append("名前:" + stage.name).append("\n")
                            .append("足場数:" + stage.footings.size()).append("\n");
                    commandSender.sendMessage(stringBuilder.toString());
                });

        CommandAPICommand play = new CommandAPICommand("play")

                .withSubcommand(new CommandAPICommand("on")
                        .executes((commandSender, objects) -> {
                            MainManager mainManager = ShortcutRunPlugin.getInstance().getMainManager();
                            if (mainManager.getSelectedStage() == null) {
                                commandSender.sendMessage(ChatColor.RED + "ステージが選択されていません！");
                                return;
                            }
                            if (mainManager.isEditing()) {
                                commandSender.sendMessage(ChatColor.RED + "編集中にプレイはできません！");
                                return;
                            } else if (mainManager.isPlaying()) {
                                commandSender.sendMessage(ChatColor.RED + "現在既にプレイ中です！");
                                return;
                            }
                            mainManager.setPlaying(true);
                            commandSender.sendMessage(ChatColor.GREEN + "プレイ開始しました");
                            return;

                        })
                )

                .withSubcommand(new CommandAPICommand("off")
                        .executes((commandSender, objects) -> {
                            MainManager mainManager = ShortcutRunPlugin.getInstance().getMainManager();
                            if (!mainManager.isPlaying()) {
                                commandSender.sendMessage(ChatColor.RED + "現在はプレイ中ではありません！");
                                return;
                            }
                            mainManager.setPlaying(false);
                            mainManager.reset();
                            commandSender.sendMessage(ChatColor.GREEN + "プレイ終了しました");
                            return;

                        })
                );

        CommandAPICommand config = new CommandAPICommand("config")

                .withSubcommand(new CommandAPICommand("reset")
                        .executes((commandSender, objects) -> {
                            ShortcutRunPlugin.getInstance().getConfigration().setToDefault();
                            commandSender.sendMessage(ChatColor.GREEN + "全ての設定項目を初期値に設定しました");
                        })
                )

                .withSubcommand(new CommandAPICommand("get")
                        .withArguments(new ConfigItemArgument("configItem"))
                        .executes((commandSender, objects) -> {
                            ConfigItem configItem = (ConfigItem) objects[0];
                            Object value = ShortcutRunPlugin
                                    .getInstance()
                                    .getConfigration()
                                    .get(configItem);
                            commandSender.sendMessage(configItem.getDescription() + ": " + value + " (初期値: " + configItem.getDefaultValue() + ")");
                        })
                )

                .withSubcommand(new CommandAPICommand("set")
                        .withArguments(new ConfigItemArgument("configItem"))
                        .withArguments(new StringArgument("value").overrideSuggestions((sender, objects) -> {
                            ConfigItem configItem = (ConfigItem) objects[0];
                            if (configItem.getDefaultValue() instanceof Boolean) {
                                return new String[] { "true" , "false" };
                            } else {
                                return new String[] {};
                            }
                        }))
                        .executes((commandSender, objects) -> {
                            ConfigItem configItem = (ConfigItem) objects[0];
                            Object value = objects[1];
                            Configration configration = ShortcutRunPlugin.getInstance().getConfigration();
                            if (configItem.isValid(value)) {
                                configration.set(configItem, value);
                                commandSender.sendMessage(ChatColor.GREEN + configItem.getConfigKey() + " の値を " + configItem.cast(value) + " に設定しました");
                            } else {
                                commandSender.sendMessage(ChatColor.RED + "" + value + " は無効な値です！\n" + configItem.getConfigKey() + ": " + configItem.getCondition());
                            }
                        })
                );


        new CommandAPICommand("shortcutrun")
                .withPermission(CommandPermission.OP)
                .withSubcommand(edit)
                .withSubcommand(play)
                .withSubcommand(select)
                .withSubcommand(config)
                .withSubcommand(stageInfo)
                .register();
    }

    public static void unregister() {
        CommandAPI.unregister("shortcutrun", true);
    }

}
