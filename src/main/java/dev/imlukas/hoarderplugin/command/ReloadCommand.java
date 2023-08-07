package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.command.command.impl.AdvancedCommand;
import dev.imlukas.hoarderplugin.utils.command.command.impl.ExecutionContext;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements SimpleCommand {

    private final HoarderPlugin plugin;

    public ReloadCommand(HoarderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "hoarder.reload";
    }

    @Override
    public String getPermission() {
        return "hoarder.reload";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        plugin.reload();

        plugin.getMessages().sendMessage(sender, "reload");
    }
}
