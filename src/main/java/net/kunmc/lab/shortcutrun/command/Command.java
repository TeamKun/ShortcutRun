package net.kunmc.lab.shortcutrun.command;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.command.argument.ConfigItemArgument;
import net.kunmc.lab.shortcutrun.command.argument.StageArgument;
import net.kunmc.lab.shortcutrun.config.ConfigItem;
import net.kunmc.lab.shortcutrun.config.Configration;
import net.kunmc.lab.shortcutrun.gameobject.Stage;
import net.kunmc.lab.shortcutrun.manager.MainManager;
import net.kunmc.lab.shortcutrun.manager.StageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Command {

    public static void register() {

        CommandAPICommand edit = new CommandAPICommand("edit")

                .withPermission("shortcutrun.command.edit")

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

                .withSubcommand(new CommandAPICommand("giveEditItem")

                        .withArguments(new EntitySelectorArgument("target", EntitySelectorArgument.EntitySelector.MANY_PLAYERS))
                        .executes((commandSender, objects) -> {
                            Collection<Player> players = (Collection<Player>) objects[0];

                            players.forEach(player -> {
                                MainManager.giveEditItems(player);
                                player.sendMessage(ChatColor.GREEN + "ステージ編集用アイテムを受け取りました");
                            });

                            if (players.size() == 0) {
                                commandSender.sendMessage(ChatColor.RED + "プレイヤーが見つかりませんでした");
                            } else if (players.size() == 1) {
                                commandSender.sendMessage(ChatColor.GREEN + players.iterator().next().getName() + "にステージ編集用アイテムを与えました");
                            } else {
                                commandSender.sendMessage(ChatColor.GREEN + "" + players.size() + "人のプレイヤーにステージ編集用アイテムを与えました");
                            }
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

                .withPermission("shortcutrun.command.select")

                .withArguments(new StageArgument("stage"))
                .executes((commandSender, objects) -> {
                    ShortcutRunPlugin
                            .getInstance()
                            .getMainManager()
                            .setStage((Stage) objects[0]);
                    commandSender.sendMessage("ステージを選択しました");
                });

        CommandAPICommand play = new CommandAPICommand("play")

                .withPermission("shortcutrun.command.play")

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
                            if (Configration.resetOnFinish.get()) {

                                Bukkit.getOnlinePlayers().forEach(player -> mainManager.setFootingAmount(player, 0));

                            }
                            commandSender.sendMessage(ChatColor.GREEN + "プレイ終了しました");
                            return;

                        })
                );

        CommandAPICommand config = new CommandAPICommand("config")

                .withPermission("shortcutrun.command.config")

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
                .withSubcommand(edit)
                .withSubcommand(play)
                .withSubcommand(select)
                .withSubcommand(config)
                .register();
    }

    public static void unregister() {
        CommandAPI.unregister("shortcutrun", true);
    }

}
