package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.impl.Event;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.menus.HoarderSellMenu;
import dev.imlukas.hoarderplugin.utils.command.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoarderRewardsCommand implements SimpleCommand {

    private final HoarderPlugin plugin;
    private final EventRegistry eventRegistry;
    private final Messages messages;

    public HoarderRewardsCommand(HoarderPlugin plugin) {
        this.plugin = plugin;
        this.eventRegistry = plugin.getEventRegistry();
        this.messages = plugin.getMessages();
    }
    @Override
    public String getIdentifier() {
        return "hoarder.rewards";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (eventRegistry.getLastEvent() == null) {
            messages.sendMessage(sender, "command.no-event");
            return;
        }

        Event activeEvent = eventRegistry.getActiveEvent();
        new HoarderSellMenu(plugin, (Player) sender, activeEvent).open();
    }
}
