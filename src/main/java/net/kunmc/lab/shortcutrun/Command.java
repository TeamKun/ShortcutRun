package net.kunmc.lab.shortcutrun;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

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

        if (args[0].equalsIgnoreCase("placeFooting")) {
            sender.sendMessage("作成中...");
        } else if (args[0].equalsIgnoreCase("reload")) {
            Bukkit.getOnlinePlayers().forEach(player -> shortcutrun.renderFooting(player));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {

        List<String> suggestions = null;

        switch (args.length) {
            case 1:
                suggestions = new ArrayList<>(Arrays.asList("placeFooting", "reload"));
                break;
            default:
                break;
        }

        return suggestions;
    }
}
