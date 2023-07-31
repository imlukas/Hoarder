package dev.imlukas.hoarderplugin.menus.editors;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.utils.item.ItemUtil;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.menu.selection.Selection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PrizeListMenu extends UpdatableMenu {

    private final PrizeRegistry prizeRegistry;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private PaginableArea area;

    public PrizeListMenu(HoarderPlugin plugin, Player viewer) {
        super(plugin, viewer);
        this.prizeRegistry = plugin.getPrizeRegistry();
        setup();
    }

    @Override
    public void refresh() {
        area.clear();
        for (EventPrize eventPrize : prizeRegistry.getPrizes().values()) {
            Button button = new Button(applicator.getItem("item"));

            ItemStack displayItem = eventPrize.getDisplayItem();
            if (displayItem != null) {
                button.setDisplayItem(displayItem);
            }

            if (hasPermission()) {
                ItemUtil.addLore(button.getDisplayItem(), "\n&7Left-Click to Edit this prize");
                button.setLeftClickAction(() -> new PrizeEditorMenu(getPlugin(), getViewer(), eventPrize).open());
            }

            area.addElement(button);
        }

        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = getApplicator();

        BaseLayer layer = new BaseLayer(menu);
        PaginableLayer paginableLayer = new PaginableLayer(menu);
        area = new PaginableArea(applicator.getMask().selection("."));
        paginableLayer.addArea(area);

        menu.addRenderable(layer, paginableLayer);

        applicator.registerButton(layer, "c", this::close);

        if (hasPermission()) {
            Button createButton = new Button(applicator.getItem("create"));
            createButton.setLeftClickAction(() -> new PrizeCreatorMenu(getPlugin(), getViewer()).open());

           Selection selection = getApplicator().getMask().selection("r");
           layer.applySelection(selection, createButton);
        }

        refresh();
    }

    @Override
    public String getIdentifier() {
        return "prize-list";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }

    public boolean hasPermission() {
        return getViewer().hasPermission("hoarder.prize.edit");
    }
}
