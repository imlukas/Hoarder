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
import dev.imlukas.hoarderplugin.utils.menu.template.FallbackMenu;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class PrizeCreatorMenu extends UpdatableMenu implements FallbackMenu{
    private final Messages messages;
    private final PrizeRegistry prizeRegistry;
    private final PrizeHandler prizeHandler;
    private final FallbackMenu fallbackMenu;

    private ConfigurableMenu menu;
    private BaseLayer layer;
    private ConfigurationApplicator applicator;

    private ItemStack displayItem;
    private String displayName, identifier;
    private LinkedList<PrizeAction> actions = new LinkedList<>();

    public PrizeCreatorMenu(HoarderPlugin plugin, Player viewer, FallbackMenu fallbackMenu) {
        super(plugin, viewer);
        this.messages = plugin.getMessages();
        this.prizeRegistry = plugin.getPrizeRegistry();
        this.prizeHandler = plugin.getPrizeHandler();
        this.fallbackMenu = fallbackMenu;

        this.displayName = "";
        this.identifier = "";
        setup();
    }

    @Override
    public void setup() {
        menu = createMenu();
        layer = new BaseLayer(menu);
        applicator = getApplicator();

        applicator.registerButton(layer, "c", fallbackMenu::openFallback);

        Button itemSelectionButton = applicator.registerButton(layer, "d");
        displayItem = itemSelectionButton.getDisplayItem().clone();
        ItemUtil.clearLore(displayItem);

        itemSelectionButton.setLeftClickAction(() -> {
            new ItemSelectionMenu(getPlugin(), getViewer(), displayItem.getType(), (newMaterial) -> {
                displayItem.setType(newMaterial);
                ScheduleBuilder.runIn1Tick(getPlugin(), this::open).sync().start();
                refresh();
            });
        });

        applicator.registerButton(layer, "n", () -> {
            messages.sendMessage(getViewer(), "inputs.display-name");
            holdForInput((displayName) -> {
                ItemUtil.setItemName(displayItem, displayName);
                this.displayName = displayName;
                refresh();
            });
        });

        applicator.registerButton(layer, "i", () -> {
            messages.sendMessage(getViewer(), "inputs.identifier");
            holdForInput((identifier) -> {
                this.identifier = identifier;
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
            if (identifier.isEmpty()) {
                identifier = "prize-" + prizeRegistry.getPrizes().size();
            }

            if (displayName.isEmpty()) {
                displayName = identifier;
            }

            if (this.displayItem == null) {
                this.displayItem = new ItemStack(Material.PAPER);
            }

            EventPrize eventPrize = new EventPrize(identifier, displayName, displayItem, actions);
            prizeRegistry.registerPrize(eventPrize);
            prizeHandler.createPrize(eventPrize);

            messages.sendMessage(getViewer(), "editors.prize-created", new Placeholder<>("prize", eventPrize.getDisplayName()));
            fallbackMenu.openFallback();
        });

        applicator.registerButton(layer, "l", () -> {
            new LoreEditorMenu(getPlugin(), getViewer(), this, displayItem);
            refresh();
        });

        menu.addRenderable(layer);
        refresh();
    }


    @Override
    public void refresh() {
        List<Placeholder<Player>> placeholderList = List.of(
                new Placeholder<>("display-name", TextUtils.color(displayName)),
                new Placeholder<>("identifier", identifier));

        layer.applySelection(applicator.getMask().selection("di"), new DecorationItem(displayItem));
        menu.setItemPlaceholders(placeholderList);
        menu.forceUpdate();
    }

    @Override
    public String getIdentifier() {
        return "prize-creator";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }

    @Override
    public void openFallback() {
        refresh();
        open();
    }
}
