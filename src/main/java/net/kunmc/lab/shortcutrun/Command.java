package net.kunmc.lab.shortcutrun;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command implements TabExecutor {

    private static String name = "shortcutrun";

    private Shortcutrun shortcutrun;

    public Command(Shortcutrun shortcutrun) {
        this.shortcutrun = shortcutrun;
    }

    public void register() {
        Bukkit.getPluginCommand(name).setExecutor(this);
        Bukkit.getPluginCommand(name).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return true;
        }

        switch (args[0]) {
            case "activate":
                if (shortcutrun.isActive()) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("既に起動しています！").toString());
                } else {
                    shortcutrun.setActive(true);
                    sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("Shortcut Run 起動").toString());
                }
                break;
            case "inactivate":
                if (!shortcutrun.isActive()) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("まだ起動していません！").toString());
                } else {
                    shortcutrun.setActive(false);
                    sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("Shortcut Run 停止").toString());
                }
                break;
            case "reload":
                Bukkit.getOnlinePlayers().forEach(player -> shortcutrun.renderFooting(player));
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("全プレイヤーの足場を再読み込みしました").toString());
                break;
            case "reloadConfig":
                shortcutrun.reloadConfig();
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("設定ファイルを再読み込みしました").toString());
                break;
            case "getFooting":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("このコマンドはプレイヤーのみ実行可能です").toString());
                } else {
                    Player player = (Player) sender;
                    List<String> itemName = new ArrayList<>(Arrays.asList("シングル", "トリプル1", "トリプル2"));
                    itemName.forEach(name -> {
                        ItemStack itemStack = new ItemStack(Material.OAK_SLAB);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(name);
                        itemStack.setItemMeta(itemMeta);
                        player.getInventory().addItem(itemStack);
                    });
                }
                break;
            case "reset":
                shortcutrun.reset();
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("足場をリセットしました").toString());
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {

        List<String> suggestions = null;

        switch (args.length) {
            case 1:
                suggestions = new ArrayList<>(Arrays.asList("activate", "inactivate", "reload", "reloadConfig", "getFooting", "reset"));
                break;
            default:
                break;
        }

        return suggestions;
    }
}
