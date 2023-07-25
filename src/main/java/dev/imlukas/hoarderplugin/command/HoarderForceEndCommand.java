package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.impl.Event;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.utils.command.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import org.bukkit.command.CommandSender;

public class HoarderForceEndCommand implements SimpleCommand {

    private final HoarderPlugin plugin;
    private final EventRegistry eventRegistry;
    private final Messages messages;

    public HoarderForceEndCommand(HoarderPlugin plugin) {
        this.plugin = plugin;
        this.eventRegistry = plugin.getEventRegistry();
        this.messages = plugin.getMessages();
    }
    @Override
    public String getIdentifier() {
        return "hoarder.sell";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (eventRegistry.getActiveEvent() == null) {
            messages.sendMessage(sender, "command.no-event");
            return;
        }

        Event activeEvent = eventRegistry.getActiveEvent();
        activeEvent.forceEnd();
    }
}
