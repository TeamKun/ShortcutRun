package net.kunmc.lab.shortcutrun.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

public class CommandTest {

    public static void register() {

        CommandAPICommand render = new CommandAPICommand("render")
                .executes((commandSender, objects) -> {
                    ShortcutRunPlugin.getInstance().getMainManager().debug();
                });

        CommandAPICommand a = new CommandAPICommand("a")
                .executes((commandSender, objects) -> {
                    Player player = (Player) commandSender;
                    FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(player.getLocation(), Material.OAK_PLANKS, (byte) 0);
                    fallingBlock.setPersistent(true);
                    fallingBlock.setInvulnerable(true);
                    fallingBlock.setGravity(false);
                    player.addPassenger(fallingBlock);
                });

        new CommandAPICommand("test")
                .withPermission(CommandPermission.OP)
                .withSubcommand(render)
                .withSubcommand(a)
                .register();
    }

}
