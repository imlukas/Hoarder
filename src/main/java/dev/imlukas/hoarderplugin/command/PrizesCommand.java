package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.impl.Event;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.menus.HoarderSellMenu;
import dev.imlukas.hoarderplugin.menus.editors.PrizeListMenu;
import dev.imlukas.hoarderplugin.utils.command.command.impl.AdvancedCommand;
import dev.imlukas.hoarderplugin.utils.command.command.impl.ExecutionContext;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
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
        new PrizeListMenu(plugin, (Player) sender).open();
    }
}
