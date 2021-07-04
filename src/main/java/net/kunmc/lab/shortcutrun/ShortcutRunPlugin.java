package net.kunmc.lab.shortcutrun;

import dev.jorel.commandapi.CommandAPI;
import net.kunmc.lab.shortcutrun.command.Command;
import net.kunmc.lab.shortcutrun.command.CommandTest;
import net.kunmc.lab.shortcutrun.listener.EditEventListener;
import net.kunmc.lab.shortcutrun.listener.RenderEventListener;
import net.kunmc.lab.shortcutrun.manager.MainManager;
import net.kunmc.lab.shortcutrun.listener.PlayEventListener;
import net.kunmc.lab.shortcutrun.manager.StageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ShortcutRunPlugin extends JavaPlugin {

    private static ShortcutRunPlugin instance;

    public ShortcutRunPlugin() {
        this.instance = this;
    }

    private MainManager mainManager;
    private StageManager stageManager;

    @Override
    public void onLoad() {
        CommandAPI.onLoad(true);
    }

    @Override
    public void onEnable() {

        saveDefaultConfig();
        reloadConfig();

        Bukkit.getPluginManager().registerEvents(new PlayEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new EditEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new RenderEventListener(), this);

        mainManager = new MainManager();
        mainManager.runTaskTimer(this, 0 ,1);
        stageManager = new StageManager();
        stageManager.load();

        CommandAPI.onEnable(this);

        Command.register();
        CommandTest.register();

    }

    @Override
    public void onDisable() {
        mainManager.reset();
        stageManager.save();
        saveConfig();
    }

    public MainManager getMainManager() {
        return mainManager;
    }

    public StageManager getStageManager() {
        return stageManager;
    }

    public static ShortcutRunPlugin getInstance() {
        return instance;
    }
}
