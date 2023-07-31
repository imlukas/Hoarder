package dev.imlukas.hoarderplugin.menus.editors;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.mask.PatternMask;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public class ActionListMenu extends UpdatableMenu {

    private final EventPrize eventPrize;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private PatternMask mask;
    private BaseLayer layer;
    private Consumer<List<PrizeAction>> afterSetup;

    public ActionListMenu(HoarderPlugin plugin, Player viewer, EventPrize eventPrize, Consumer<List<PrizeAction>> afterSetup) {
        super(plugin, viewer);
        this.eventPrize = eventPrize;

        this.afterSetup = afterSetup;
        setup();
        open();
    }

    @Override
    public void refresh() {
        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = getApplicator();
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
