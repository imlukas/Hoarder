package dev.imlukas.hoarderplugin.command.editor;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.menus.editors.settings.SettingsEditor;
import dev.imlukas.hoarderplugin.utils.command.command.impl.AdvancedCommand;
import dev.imlukas.hoarderplugin.utils.command.command.impl.ExecutionContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand extends AdvancedCommand {

    private final HoarderPlugin plugin;

    public SettingsCommand(HoarderPlugin plugin) {
        super("hoarder settings");
        this.plugin = plugin;
    }

    @Override
    public String getPermission() {
        return "hoarderplugin.settings";
    }

    @Override
    public void execute(CommandSender sender, ExecutionContext context) {
        new SettingsEditor(plugin, (Player) sender, "hoarder").open();
    }
}
