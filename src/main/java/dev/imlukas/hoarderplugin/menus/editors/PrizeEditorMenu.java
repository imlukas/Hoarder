package dev.imlukas.hoarderplugin.menus.editors;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.menus.ItemSelectionMenu;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.prize.storage.PrizeHandler;
import dev.imlukas.hoarderplugin.utils.component.ComponentEvent;
import dev.imlukas.hoarderplugin.utils.component.ComponentUtil;
import dev.imlukas.hoarderplugin.utils.item.ItemUtil;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.button.DecorationItem;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.mask.PatternMask;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class PrizeEditorMenu extends UpdatableMenu {
    private final Messages messages;
    private final ActionRegistry actionRegistry;
    private final PrizeRegistry prizeRegistry;
    private final PrizeHandler prizeHandler;
    private final EventPrize prize;

    private ConfigurableMenu menu;
    private BaseLayer layer;

    private final ItemStack displayItem;
    private final LinkedList<PrizeAction> actions = new LinkedList<>();
    private String displayName;

    public PrizeEditorMenu(HoarderPlugin plugin, Player viewer, EventPrize prize) {
        super(plugin, viewer);
        this.messages = plugin.getMessages();
        this.actionRegistry = plugin.getActionRegistry();
        this.prizeRegistry = plugin.getPrizeRegistry();
        this.prizeHandler = plugin.getPrizeHandler();
        this.prize = prize;

        this.displayItem = prize.getDisplayItem();
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

        DecorationItem prizeItem = new DecorationItem(displayItem.clone());
        ItemUtil.addLore(prizeItem.getDisplayItem(), "&7You're editing this prize.");
        layer.applySelection(applicator.getMask().selection("i"), prizeItem);

        applicator.registerButton(layer, "c", () -> new PrizeListMenu(getPlugin(), getViewer()).open());

        applicator.registerButton(layer, "del", () -> {
            prizeRegistry.unregisterPrize(prize);
            prizeHandler.removePrize(prize);

            messages.sendMessage(getViewer(), "editors.prize-deleted", new Placeholder<>("prize", prize.getDisplayName()));
        });

        Button itemButton = applicator.registerButton(layer, "d");
        itemButton.setLeftClickAction(() -> {
            new ItemSelectionMenu(getPlugin(), getViewer(), itemButton.getDisplayItem().getType(), (newMaterial) -> {
                displayItem.setType(newMaterial);

                itemButton.getDisplayItem().setType(newMaterial);
                prizeItem.getDisplayItem().setType(newMaterial);
                ScheduleBuilder.runIn1Tick(getPlugin(), this::open).sync().start();
                refresh();
            });
        });

        applicator.registerButton(layer, "n", () -> holdForInput((displayName) -> {
            this.displayName = displayName;
            refresh();
        }));

        Button actionButton = applicator.registerButton(layer, "a");
        actionButton.setClickAction((ignored) -> {
            new ActionListMenu(getPlugin(), getViewer(), prize, (actions) -> {
                this.actions.clear();
                this.actions.addAll(actions);
                ScheduleBuilder.runIn1Tick(getPlugin(), this::open).sync().start();
                refresh();
            });
        });

        applicator.registerButton(layer, "cr", () -> {
            prize.setDisplayItem(displayItem)
                    .setDisplayName(displayName)
                    .setActions(actions);

            prizeHandler.updatePrize(prize);
            messages.sendMessage(getViewer(), "editors.prize-updated");
        });
    }

    @Override
    public void refresh() {
        List<Placeholder<Player>> placeholderList = List.of(
                new Placeholder<>("display-name", displayName),
                new Placeholder<>("identifier", prize.getIdentifier()));

        menu.setItemPlaceholders(placeholderList);
        layer.setItemPlaceholders(placeholderList);
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
