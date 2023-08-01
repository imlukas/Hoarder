package dev.imlukas.hoarderplugin.menus.editors;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LoreEditor extends UpdatableMenu {

    private final ItemStack displayItem;

    private ConfigurableMenu menu;
    private BaseLayer layer;
    private ConfigurationApplicator applicator;

    public LoreEditor(HoarderPlugin plugin, Player viewer, ItemStack displayItem) {
        super(plugin, viewer);
        this.displayItem = displayItem;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void setup() {

    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public ConfigurableMenu getMenu() {
        return null;
    }
}
