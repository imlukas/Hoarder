package dev.imlukas.hoarderplugin.utils.menu.template;

import dev.imlukas.hoarderplugin.utils.concurrency.MainThreadExecutor;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface Menu {

    /**
     * Handles creation of the menu, definition of variables and static button creation.
     */
    void setup();
    Player getViewer();

    String getIdentifier();

    ConfigurableMenu getMenu();

    default ConfigurationApplicator getApplicator() {
        return getMenu().getApplicator();
    }

    default void close() {
        Player viewer = getViewer();

        if (viewer.getOpenInventory().getTopInventory().equals(getMenu().getInventory())) {
            if (Bukkit.isPrimaryThread()) {
                viewer.closeInventory();
            } else {
                MainThreadExecutor.INSTANCE.execute(viewer::closeInventory); // fuck you bukkit
            }
        }
    }

    default void open() {
        getMenu().open();
    }
}
