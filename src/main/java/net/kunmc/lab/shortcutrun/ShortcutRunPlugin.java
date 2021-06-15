package net.kunmc.lab.shortcutrun;

import net.kunmc.lab.shortcutrun.core.StageList;
import net.kunmc.lab.shortcutrun.exception.ConfigLoadException;
import net.kunmc.lab.shortcutrun.play.GameManager;
import net.kunmc.lab.shortcutrun.play.PlayEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ShortcutRunPlugin extends JavaPlugin {

    private static ShortcutRunPlugin instance;

    public ShortcutRunPlugin(ShortcutRunPlugin instance) {
        this.instance = instance;
    }

    private StageList stageList;
    private GameManager gameManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        reloadConfig();

        Bukkit.getPluginManager().registerEvents(new PlayEventListener(), this);

        stageList = new StageList();
        try {
            stageList.load();
        } catch (ConfigLoadException e) {
            e.printStackTrace();
        }

        gameManager = new GameManager();
        gameManager.runTaskTimer(this, 0, 1);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public static ShortcutRunPlugin getInstance() {
        return instance;
    }
}
