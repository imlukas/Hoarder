package dev.imlukas.hoarderplugin.menus.editors;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.utils.item.ItemUtil;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.menu.template.FallbackMenu;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class LoreEditorMenu extends UpdatableMenu {

    private final Messages messages;
    private final FallbackMenu fallbackMenu;

    private final ItemStack displayItem;
    private final List<String> lore;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private PaginableArea area;

    public LoreEditorMenu(HoarderPlugin plugin, Player viewer, FallbackMenu fallbackMenu, ItemStack displayItem) {
        super(plugin, viewer);
        this.messages = plugin.getMessages();
        this.fallbackMenu = fallbackMenu;

        this.displayItem = displayItem;
        this.lore = displayItem.getLore() == null ? new ArrayList<>() : displayItem.getLore();
        setup();
        open();
    }

    @Override
    public void refresh() {
        area.clear();

        for (int i = 0; i < lore.size(); i++) {
            String line = lore.get(i);
            Placeholder<Player> linePlaceholder = new Placeholder<>("line", line);
            Button button = applicator.makeButton("item");
            button.setItemPlaceholders(linePlaceholder);
            int finalIndex = i;

            button.setMiddleClickAction(() -> {
                messages.sendMessage(getViewer(), "inputs.order", new Placeholder<>("max", String.valueOf(lore.size())));
                holdForInput((input) -> {
                    int newIndex = TextUtils.parseInt(input) - 1;

                    if (newIndex < 0 || newIndex >= lore.size()) {
                        messages.sendMessage(getViewer(), "editors.invalid-index");
                        return;
                    }

                    lore.remove(finalIndex);
                    lore.add(newIndex, line);
                    messages.sendMessage(getViewer(), "editors.lore.order-change",
                            linePlaceholder,
                            new Placeholder<>("new-order", input));
                    refresh();
                });
            });

            button.setLeftClickAction(() -> {
                messages.sendMessage(getViewer(), "inputs.line");
                holdForInput((input) -> {
                    lore.set(finalIndex, input);
                    messages.sendMessage(getViewer(), "editors.lore.changed", linePlaceholder, new Placeholder<>("line", input));
                    refresh();
                });
            });

            button.setRightClickAction(() -> {
                messages.sendMessage(getViewer(), "editors.lore.removed", linePlaceholder);
                lore.remove(finalIndex);
                refresh();
            });

            area.addElement(button);
        }

        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = getApplicator();
        area = new PaginableArea(applicator.getMask().selection("."));

        BaseLayer layer = new BaseLayer(menu);
        PaginableLayer paginableLayer = new PaginableLayer(menu);
        paginableLayer.addArea(area);

        applicator.registerButton(layer, "p", paginableLayer::previousPage);
        applicator.registerButton(layer, "n", paginableLayer::nextPage);
        applicator.registerButton(layer, "c", () -> {
            ItemUtil.setLore(displayItem, lore);
            fallbackMenu.openFallback();
            messages.sendMessage(getViewer(), "editors.lore.updated");
        });

        applicator.registerButton(layer, "cr", () -> {
            holdForInput((newLine) -> {
                lore.add(TextUtils.color(newLine));
                messages.sendMessage(getViewer(), "editors.lore.added", new Placeholder<>("line", newLine));
                refresh();
            });
        });

        menu.addRenderable(layer, paginableLayer);
        refresh();
    }

    @Override
    public String getIdentifier() {
        return "lore-editor";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }
}
