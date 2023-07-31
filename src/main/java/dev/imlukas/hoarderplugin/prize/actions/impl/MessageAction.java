package dev.imlukas.hoarderplugin.prize.actions.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.entity.Player;

public class MessageAction implements PrizeAction {

    private final String message;
    private final Messages messages;

    public MessageAction(HoarderPlugin plugin, String message) {
        this.message = message;
        this.messages = plugin.getMessages();
    }

    @Override
    public void handle(Player player) {
        String message = messages.getMessage(this.message);

        if (message == null) {
            message = this.message;
        }

        message.replace("%player%", player.getName());
        player.sendMessage(TextUtils.color(message));
    }

    @Override
    public String getIdentifier() {
        return "MESSAGE";
    }

    @Override
    public String getInput() {
        return message;
    }

    @Override
    public String getFullInput() {
        return getIdentifier() + ":" + getInput();
    }
}
