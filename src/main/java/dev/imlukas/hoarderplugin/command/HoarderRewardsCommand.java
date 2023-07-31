package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.menus.HoarderRewardsMenu;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoarderRewardsCommand implements SimpleCommand {

    private final HoarderPlugin plugin;
    private final EventTracker eventTracker;
    private final Messages messages;

    public HoarderRewardsCommand(HoarderPlugin plugin) {
        this.plugin = plugin;
        this.eventTracker = plugin.getEventTracker();
        this.messages = plugin.getMessages();
    }

    @Override
    public String getIdentifier() {
        return "hoarder.rewards";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (eventTracker.getLastEvent() == null) {
            messages.sendMessage(sender, "command.no-event");
            return;
        }

        Event lastEvent = eventTracker.getLastEvent();
        new HoarderRewardsMenu(plugin, (Player) sender, (HoarderEvent) lastEvent).open();
    }
}
