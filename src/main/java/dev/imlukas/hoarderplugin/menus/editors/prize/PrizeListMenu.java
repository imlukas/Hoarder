package dev.imlukas.hoarderplugin.menus.editors.prize;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.prize.storage.PrizeHandler;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.menu.selection.Selection;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PrizeListMenu extends UpdatableMenu {

    private final PrizeHandler prizeHandler;
    private final PrizeRegistry prizeRegistry;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private PaginableArea area;

    public PrizeListMenu(HoarderPlugin plugin, Player viewer) {
        super(plugin, viewer);
        this.prizeHandler = plugin.getPrizeHandler();
        this.prizeRegistry = plugin.getPrizeRegistry();
        setup();
    }

    @Override
    public void refresh() {
        area.clear();
        for (EventPrize eventPrize : prizeRegistry.getPrizes()) {
            Button button = new Button(applicator.getItem("item").clone());

            ItemStack displayItem = eventPrize.getDisplayItem();

            button.setDisplayItem(displayItem.clone());

            if (hasPermission()) {
                button.setClickWithItemTask((itemStack) -> {
                    if (itemStack == null || itemStack.getType().isAir()) {
                        return;
                    }

                    updatePrizeItem(eventPrize, itemStack);
                });

                button.setRightClickAction(() -> {
                    prizeRegistry.unregisterPrize(eventPrize);
                    prizeHandler.removePrize(eventPrize).thenRun(this::refresh);
                });
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

        applicator.registerButton(layer, "p", paginableLayer::previousPage);
        applicator.registerButton(layer, "n", paginableLayer::nextPage);
        applicator.registerButton(layer, "c", this::close);

        if (hasPermission()) {
            Button createButton = new Button(applicator.getItem("create"));
            createButton.setClickWithItemTask((itemStack) -> {

                if (itemStack == null || itemStack.getType().isAir()) {
                    return;
                }

                createPrizeItem(itemStack);
            });

            Selection selection = getApplicator().getMask().selection("r");
            layer.applySelection(selection, createButton);
        }
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

    public void updatePrizeItem(EventPrize eventPrize, ItemStack itemStack) {
        eventPrize.setDisplayItem(itemStack.clone());
        prizeHandler.updatePrize(eventPrize).thenRun(this::refresh);
    }

    public void createPrizeItem(ItemStack itemStack) {
        String displayName = itemStack.getItemMeta().getDisplayName().isEmpty() ? TextUtils.enumToText(itemStack.getType()) : itemStack.getItemMeta().getDisplayName();
        EventPrize eventPrize = new EventPrize(displayName, itemStack.clone());
        prizeRegistry.registerPrize(eventPrize);
        prizeHandler.createPrize(eventPrize).thenRun(this::refresh);
    }
}
