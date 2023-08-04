package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.menus.HoarderMainMenu;
import dev.imlukas.hoarderplugin.utils.command.command.impl.AdvancedCommand;
import dev.imlukas.hoarderplugin.utils.command.command.impl.ExecutionContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoarderCommand extends AdvancedCommand {

    private final HoarderPlugin plugin;

    public HoarderCommand(HoarderPlugin plugin) {
        super("hoarder");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, ExecutionContext context) {
        new HoarderMainMenu(plugin, (Player) sender).open();
    }
}
