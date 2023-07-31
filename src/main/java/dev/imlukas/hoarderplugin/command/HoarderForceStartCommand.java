package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import org.bukkit.command.CommandSender;

public class HoarderForceStartCommand implements SimpleCommand {
    private final HoarderPlugin plugin;

    public HoarderForceStartCommand(HoarderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "hoarder.forcestart";
    }

    @Override
    public String getPermission() {
        return "hoarder.admin";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        Event activeEvent = plugin.getEventTracker().getActiveEvent();

        if (activeEvent != null) {
            activeEvent.forceEnd();
        }

        new HoarderEvent(plugin);
    }
}
