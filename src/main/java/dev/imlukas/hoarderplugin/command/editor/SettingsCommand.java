package dev.imlukas.hoarderplugin.command.editor;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.menus.editors.prize.PrizeListMenu;
import dev.imlukas.hoarderplugin.menus.editors.settings.SettingsEditor;
import dev.imlukas.hoarderplugin.utils.command.command.impl.AdvancedCommand;
import dev.imlukas.hoarderplugin.utils.command.command.impl.ExecutionContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand extends AdvancedCommand {

    private final HoarderPlugin plugin;

    public SettingsCommand(HoarderPlugin plugin) {
        super("settings");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, ExecutionContext context) {
        new SettingsEditor(plugin, (Player) sender, "hoarder").open();
    }
}
