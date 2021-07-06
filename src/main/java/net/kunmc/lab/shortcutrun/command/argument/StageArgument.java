package net.kunmc.lab.shortcutrun.command.argument;

import dev.jorel.commandapi.arguments.CustomArgument;
import net.kunmc.lab.shortcutrun.ShortcutRunPlugin;
import net.kunmc.lab.shortcutrun.gameobject.Stage;

public class StageArgument extends CustomArgument<Stage> {

    public StageArgument(String nodeName) {
        super(nodeName, (input) -> {
            Stage stage = ShortcutRunPlugin.getInstance().getStageManager().findStageFromName(input);

            if(stage == null) {
                throw new CustomArgumentException(new MessageBuilder("存在しないステージです: ").appendArgInput());
            }
            return stage;
        });
        overrideSuggestions(sender -> ShortcutRunPlugin.getInstance().getStageManager().getStages().stream().map(stage -> stage.name).toArray(String[]::new));
    }
}
