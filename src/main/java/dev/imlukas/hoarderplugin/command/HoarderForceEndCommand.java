package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import org.bukkit.command.CommandSender;

public class HoarderForceEndCommand implements SimpleCommand {

    private final EventTracker eventTracker;
    private final Messages messages;

    public HoarderForceEndCommand(HoarderPlugin plugin) {
        this.eventTracker = plugin.getEventTracker();
        this.messages = plugin.getMessages();
    }

    @Override
    public String getIdentifier() {
        return "hoarder.forceend";
    }

    @Override
    public String getPermission() {
        return "hoarder.admin";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (eventTracker.getActiveEvent() == null) {
            messages.sendMessage(sender, "command.no-event");
            return;
        }

        Event activeEvent = eventTracker.getActiveEvent();
        activeEvent.forceEnd();
        messages.sendMessage(sender, "hoarder.force-end");
    }
}
