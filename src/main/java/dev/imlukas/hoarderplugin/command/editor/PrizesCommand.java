package dev.imlukas.hoarderplugin.command.editor;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.menus.editors.prize.PrizeListMenu;
import dev.imlukas.hoarderplugin.utils.command.command.impl.AdvancedCommand;
import dev.imlukas.hoarderplugin.utils.command.command.impl.ExecutionContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrizesCommand extends AdvancedCommand {

    private final HoarderPlugin plugin;

    public PrizesCommand(HoarderPlugin plugin) {
        super("prizes");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, ExecutionContext context) {
        new PrizeListMenu(plugin, (Player) sender).openFallback();
    }
}
