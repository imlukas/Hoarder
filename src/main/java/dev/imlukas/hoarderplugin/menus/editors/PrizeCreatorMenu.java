package dev.imlukas.hoarderplugin.menus.editors;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.menus.ItemSelectionMenu;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.prize.storage.PrizeHandler;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.mask.PatternMask;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.menu.template.Menu;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class PrizeCreatorMenu extends UpdatableMenu {
    private final Messages messages;
    private final ActionRegistry actionRegistry;
    private final PrizeRegistry prizeRegistry;
    private final PrizeHandler prizeHandler;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private PatternMask mask;
    private BaseLayer layer;

    private ItemStack displayItem;
    private String displayName, identifier;
    private LinkedList<PrizeAction> actions = new LinkedList<>();

    public PrizeCreatorMenu(HoarderPlugin plugin, Player viewer) {
        super(plugin, viewer);
        this.messages = plugin.getMessages();
        this.actionRegistry = plugin.getActionRegistry();
        this.prizeRegistry = plugin.getPrizeRegistry();
        this.prizeHandler = plugin.getPrizeHandler();
        setup();

    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = getApplicator();
        mask = applicator.getMask();
        layer = new BaseLayer(menu);

        applicator.registerButton(layer, "c", () -> new PrizeListMenu(getPlugin(), getViewer()).open());

        Button displayItem = applicator.registerButton(layer, "i");
        displayItem.setLeftClickAction(() -> {
            new ItemSelectionMenu(getPlugin(), getViewer(), displayItem.getDisplayItem().getType(), (newMaterial) -> {
                displayItem.getDisplayItem().setType(newMaterial);
                refresh();
            });
        });

        applicator.registerButton(layer, "n", () -> holdForInput((displayName) -> {
            this.displayName = displayName;
            refresh();
        }));

        applicator.registerButton(layer, "i", () -> holdForInput((identifier) -> {
            this.identifier = identifier;
            refresh();
        }));

        Button actionButton = applicator.registerButton(layer, "a");
        actionButton.setLeftClickAction(() -> {
            messages.sendMessage(getViewer(), "editors.available-action");
            holdForInput((action) -> {
                PrizeAction prizeAction = actionRegistry.getAction(action);

                if (prizeAction == null) {
                    messages.sendMessage(getViewer(), "editors.invalid-action");
                    return;
                }

                actions.add(prizeAction);
            });
        });

        actionButton.setRightClickAction(() -> {
            if (actions.isEmpty()) {
                return;
            }

            for (PrizeAction action : actions) {
                messages.sendMessage(getViewer(), "editors.action", new Placeholder<>("action", action.getIdentifier()),
                        new Placeholder<>("index", String.valueOf(actions.indexOf(action))));
            }

            holdForInput((index) -> {
                try {
                    int i = Integer.parseInt(index);
                    actions.remove(i);
                } catch (NumberFormatException e) {
                    messages.sendMessage(getViewer(), "editors.invalid-index");
                }
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

            EventPrize eventPrize = new EventPrize(identifier, displayName, displayItem.getDisplayItem(), actions);
            prizeRegistry.registerPrize(eventPrize);
            prizeHandler.createPrize(eventPrize);

            messages.sendMessage(getViewer(), "editors.prize-created");
        });
    }


    @Override
    public void refresh() {
        List<Placeholder<Player>> placeholderList = List.of(
                new Placeholder<>("display-name", displayName),
                new Placeholder<>("identifier", identifier));

        menu.setItemPlaceholders(placeholderList);
        layer.setItemPlaceholders(placeholderList);
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
}
