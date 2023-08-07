package dev.imlukas.hoarderplugin.menus.editors.prize;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.menus.ItemSelectionMenu;
import dev.imlukas.hoarderplugin.menus.editors.LoreEditorMenu;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.prize.storage.PrizeHandler;
import dev.imlukas.hoarderplugin.utils.item.ItemUtil;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.button.DecorationItem;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedList;
import java.util.List;

public class PrizeEditorMenu extends UpdatableMenu {
    private final Messages messages;
    private final PrizeRegistry prizeRegistry;
    private final PrizeHandler prizeHandler;
    private final EventPrize prize;

    private ConfigurableMenu menu;
    private BaseLayer layer;

    private final ItemStack displayItem;
    private LinkedList<PrizeAction> actions = new LinkedList<>();
    private String displayName;
    private DecorationItem prizeItem;

    public PrizeEditorMenu(HoarderPlugin plugin, Player viewer, EventPrize prize) {
        super(plugin, viewer);
        this.messages = plugin.getMessages();
        this.prizeRegistry = plugin.getPrizeRegistry();
        this.prizeHandler = plugin.getPrizeHandler();
        this.prize = prize;

        this.displayItem = prize.getDisplayItem().clone();

        this.displayName = prize.getDisplayName();
        this.actions.addAll(prize.getActions());
        setup();

    }

    @Override
    public void setup() {
        menu = createMenu();
        ConfigurationApplicator applicator = getApplicator();
        layer = new BaseLayer(menu);
        menu.addRenderable(layer);

        applicator.registerButton(layer, "c", this::close);

        applicator.registerButton(layer, "del", () -> {
            prizeRegistry.unregisterPrize(prize);
            prizeHandler.removePrize(prize);

            messages.sendMessage(getViewer(), "editors.prize-deleted", new Placeholder<>("prize", prize.getDisplayName()));
            this.close();
        });

        Button itemButton = applicator.registerButton(layer, "d");
        itemButton.getDisplayItem().setType(displayItem.getType());
        itemButton.setLeftClickAction(() -> {
            new ItemSelectionMenu(getPlugin(), getViewer(), itemButton.getDisplayItem(), (newItem) -> {
                displayItem.setType(newItem.getType());

                ItemMeta meta = newItem.getItemMeta();

                if (meta.hasCustomModelData()) {
                    ItemUtil.setModelData(displayItem, meta.getCustomModelData());
                }

                itemButton.getDisplayItem().setType(newItem.getType());
                prizeItem.getDisplayItem().setType(newItem.getType());
                ScheduleBuilder.runIn1Tick(getPlugin(), this::open).sync().start();
                refresh();
            }).onClose(this::open);
        });

        itemButton.setClickWithItemTask((newItem) -> {
            displayItem.setType(newItem.getType());

            itemButton.getDisplayItem().setType(newItem.getType());
            prizeItem.getDisplayItem().setType(newItem.getType());
            ScheduleBuilder.runIn1Tick(getPlugin(), this::open).sync().start();
            refresh();
        });

        applicator.registerButton(layer, "n", () -> {
            messages.sendMessage(getViewer(), "inputs.display-name");
            holdForInput((displayName) -> {
                this.displayName = displayName;
                ItemUtil.setItemName(displayItem, TextUtils.color(displayName));
                refresh();
            });
        });

        Button actionButton = applicator.registerButton(layer, "a");
        actionButton.setClickAction((ignored) -> {
            new ActionCreatorMenu(getPlugin(), getViewer(), this.actions, (actions) -> {
                this.actions = actions;
                ScheduleBuilder.runIn1Tick(getPlugin(), this::open).sync().start();
                refresh();
            });
        });

        applicator.registerButton(layer, "cr", () -> {
            prize.setDisplayItem(displayItem)
                    .setDisplayName(displayName)
                    .setActions(actions);

            prizeHandler.updatePrize(prize);
            messages.sendMessage(getViewer(), "editors.prize-updated", new Placeholder<>("prize", prize.getDisplayName()));
            this.close();
        });

        applicator.registerButton(layer, "l", () -> {
            new LoreEditorMenu(getPlugin(), getViewer(), displayItem).onClose(this::open).open();
            refresh();
        });

        refresh();
    }

    @Override
    public void refresh() {
        List<Placeholder<Player>> placeholderList = List.of(
                new Placeholder<>("display-name", TextUtils.color(displayName)),
                new Placeholder<>("identifier", prize.getIdentifier()));

        ItemStack displayItem = this.displayItem.clone();

        prizeItem = new DecorationItem(displayItem);
        // ItemUtil.addLore(displayItem, "&7You're editing this prize.");
        layer.applySelection(getApplicator().getMask().selection("i"), prizeItem);

        menu.setItemPlaceholders(placeholderList);
        menu.forceUpdate();
    }

    @Override
    public String getIdentifier() {
        return "prize-editor";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }
}