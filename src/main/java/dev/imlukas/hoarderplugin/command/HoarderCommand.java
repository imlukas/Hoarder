package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.impl.Event;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.menus.MerchantSellMenu;
import dev.imlukas.hoarderplugin.utils.command.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoarderCommand implements SimpleCommand {

    private final EventRegistry eventRegistry;
    private final Messages messages;

    public HoarderCommand(HoarderPlugin plugin) {
        this.eventRegistry = plugin.getEventRegistry();
        this.messages = plugin.getMessages();
    }
    @Override
    public String getIdentifier() {
        return "hoarder";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (eventRegistry.getActiveEvent() == null) {
            return;
        }

        Event activeEvent = eventRegistry.getActiveEvent();

        new MerchantSellMenu((Player) sender, activeEvent).open();
    }
}
