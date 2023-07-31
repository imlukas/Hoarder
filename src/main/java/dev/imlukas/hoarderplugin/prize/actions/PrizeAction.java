package dev.imlukas.hoarderplugin.prize.actions;

import org.bukkit.entity.Player;

public interface PrizeAction {

    /**
     * Handles the action for the player
     *
     * @param player the player
     */
    void handle(Player player);

    /**
     * Returns the identifier for the action
     *
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Returns the input for the action, excluding the identifier
     *
     * @return the input
     */
    String getInput();

    /**
     * Returns the full input for the action, including the identifier
     *
     * @return the full input
     */
    String getFullInput();
}
