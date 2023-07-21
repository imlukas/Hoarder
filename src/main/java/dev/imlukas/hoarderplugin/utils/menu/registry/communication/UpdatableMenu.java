package dev.imlukas.hoarderplugin.utils.menu.registry.communication;

import dev.imlukas.hoarderplugin.utils.menu.template.Menu;

public interface UpdatableMenu extends Menu {

    /**
     * Handles refreshing placeholders and updating buttons and other elements accordingly.
     */
    void refresh();

}
