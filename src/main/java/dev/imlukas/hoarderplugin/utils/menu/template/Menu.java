package dev.imlukas.hoarderplugin.utils.menu.template;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.concurrency.MainThreadExecutor;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.registry.MenuRegistry;
import dev.imlukas.hoarderplugin.utils.menu.registry.meta.HiddenMenuTracker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class Menu {

    @Getter
    private final HoarderPlugin plugin;
    @Getter
    private final MenuRegistry menuRegistry;
    @Getter
    private final HiddenMenuTracker hiddenMenuTracker;
    @Getter
    private final UUID viewerId;
    private Runnable onClose;

    public Menu(HoarderPlugin plugin, Player viewer) {
        this.plugin = plugin;
        this.menuRegistry = plugin.getMenuRegistry();
        this.hiddenMenuTracker = plugin.getMenuRegistry().getHiddenMenuTracker();
        this.viewerId = viewer.getUniqueId();
    }

    /**
     * Handles creation of the menu, definition of variables and static button creation.
     */
    public abstract void setup();

    public abstract String getIdentifier();

    public abstract ConfigurableMenu getMenu();

    public ConfigurationApplicator getApplicator() {
        return getMenu().getApplicator();
    }

    public Player getViewer() {
        return Bukkit.getPlayer(viewerId);
    }

    public Menu onClose(Runnable onClose) {
        this.onClose = onClose;
        return this;
    }

    public void holdForInput(Consumer<String> action) {
        hiddenMenuTracker.holdForInput(getMenu(), action, true);
    }

    public void holdForInput(Consumer<String> action, boolean reOpen) {
        hiddenMenuTracker.holdForInput(getMenu(), action, reOpen);
    }

    public ConfigurableMenu createMenu() {
        return (ConfigurableMenu) menuRegistry.create(getIdentifier(), getViewer());
    }

    public void close() {
        Player viewer = getViewer();

        if (viewer.getOpenInventory().getTopInventory().equals(getMenu().getInventory())) {
            if (Bukkit.isPrimaryThread()) {
                viewer.closeInventory();
            } else {
                MainThreadExecutor.INSTANCE.execute(viewer::closeInventory); // fuck you bukkit
            }

            if (onClose != null) {
                onClose.run();
            }
        }
    }

    public void open() {
        getMenu().open();
    }
}
