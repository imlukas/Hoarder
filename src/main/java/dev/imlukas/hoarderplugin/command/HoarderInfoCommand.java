package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.impl.Event;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.menus.HoarderSellMenu;
import dev.imlukas.hoarderplugin.utils.command.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoarderInfoCommand implements SimpleCommand {

    private final Messages messages;

    public HoarderInfoCommand(HoarderPlugin plugin) {
        this.messages = plugin.getMessages();
    }
    @Override
    public String getIdentifier() {
        return "hoarder.info";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        messages.sendMessage(sender, "hoarder.info");
    }
}
