package net.kunmc.lab.shortcutrun;

import dev.jorel.commandapi.CommandAPI;
import net.kunmc.lab.shortcutrun.command.Command;
import net.kunmc.lab.shortcutrun.config.Configration;
import net.kunmc.lab.shortcutrun.listener.EditEventListener;
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

    private Configration configration;

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

        mainManager = new MainManager();
        mainManager.runTaskTimer(this, 0 ,1);
        stageManager = new StageManager();
        stageManager.load();

        configration = new Configration();
        configration.load();

        CommandAPI.onEnable(this);

        Command.register();

    }

    @Override
    public void onDisable() {
        mainManager.reset();
        stageManager.save();
        saveConfig();
        Command.unregister();
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

    public Configration getConfigration() {
        return configration;
    }
}
