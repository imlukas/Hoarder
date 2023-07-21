package dev.imlukas.hoarderplugin.prize.actions.impl;

import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.entity.Player;

public class MessageAction implements PrizeAction {

    private final String message;

    public MessageAction(String message) {
        this.message = message;
    }

    @Override
    public void handle(Player player) {
        player.sendMessage(TextUtils.color(this.message));
    }
}
