package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import org.bukkit.command.CommandSender;

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
