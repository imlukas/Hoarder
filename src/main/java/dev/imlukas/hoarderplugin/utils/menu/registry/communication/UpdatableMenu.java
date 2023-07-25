package dev.imlukas.hoarderplugin.utils.menu.registry.communication;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.menu.template.Menu;
import org.bukkit.entity.Player;

public abstract class UpdatableMenu extends Menu {

    public UpdatableMenu(HoarderPlugin plugin, Player viewer) {
        super(plugin, viewer);
    }

    /**
     * Handles refreshing placeholders and updating buttons and other elements accordingly.
     */
    public abstract void refresh();
}
