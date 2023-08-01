package dev.imlukas.hoarderplugin.prize.actions.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.entity.Player;

public class MessageAction implements PrizeAction {

    private String input;
    private final Messages messages;

    public MessageAction(HoarderPlugin plugin, String message) {
        this.input = message;
        this.messages = plugin.getMessages();
    }

    @Override
    public void handle(Player player) {
        String message = messages.getMessage(this.input);

        if (message == null) {
            message = this.input;
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
        return input;
    }

    @Override
    public String getFullInput() {
        return getIdentifier() + ":" + getInput();
    }

    @Override
    public void setInput(String input) {
        this.input = input;
    }
}
