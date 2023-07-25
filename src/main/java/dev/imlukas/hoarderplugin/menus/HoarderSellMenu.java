package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.HoarderEventData;
import dev.imlukas.hoarderplugin.event.data.player.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.impl.Event;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HoarderSellMenu extends UpdatableMenu {

    private final Messages messages;
    private final Economy economy;
    private final HoarderEvent activeEvent;
    private int itemsSold;
    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private BaseLayer layer;

    public HoarderSellMenu(HoarderPlugin plugin, Player viewer, Event activeEvent) {
        super(plugin, viewer);
        this.activeEvent = (HoarderEvent) activeEvent;
        this.economy = plugin.getEconomy();
        this.messages = getPlugin().getMessages();
        setup();
    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = getApplicator();
        layer = new BaseLayer(menu);
        applicator.registerButton(layer, "c", this::close);
        applicator.registerButton(layer, "s", () -> {
            updateSoldItems();

            if (itemsSold == 0) {
                messages.sendMessage(getViewer(), "sold.fail");
                return;
            }

            HoarderPlayerEventData playerData = activeEvent.getEventData().getPlayerData(getViewer().getUniqueId());
            playerData.addSoldItem(itemsSold);
            double currencyAmount = activeEvent.getEventData().getActiveItem().getValue() * itemsSold;

            messages.sendMessage(getViewer(), "sold.success",
                    new Placeholder<>("amountSold", String.format("%,d", itemsSold)),
                    new Placeholder<>("soldTotal", String.format("%4.1f", currencyAmount)));

            economy.depositPlayer(getViewer(), currencyAmount);
            refresh();
        });

        menu.addRenderable(layer);
        refresh();
    }

    @Override
    public void refresh() {
        itemsSold = 0;
        removeSoldItems();
        HoarderPlayerEventData playerData = activeEvent.getEventData().getPlayerData(getViewer().getUniqueId());
        HoarderEventData eventData = activeEvent.getEventData();

        List<Placeholder<Player>> placeholderList = List.of(
                new Placeholder<>("amountSold", String.valueOf(playerData.getSoldItems())),
                new Placeholder<>("price", String.valueOf(eventData.getActiveItem().getValue())));

        Button activeItem = new Button(applicator.getItem("active"));
        activeItem.getDisplayItem().setType(eventData.getActiveItem().getMaterial());
        layer.applyRawSelection(applicator.getMask().selection("a"), activeItem);

        menu.setItemPlaceholders(placeholderList);

        menu.forceUpdate();
    }


    @Override
    public String getIdentifier() {
        return "hoarder-sell";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }


    private void removeSoldItems() {
        List<Integer> soldItemSlots = applicator.getMask().selection(".").getSlots();
        for (Integer soldItemSlot : soldItemSlots) {
            ItemStack item = menu.getInventory().getItem(soldItemSlot);

            if (item == null) {
                continue;
            }
            menu.getInventory().setItem(soldItemSlot, null);
        }
    }

    private void updateSoldItems() {
        List<Integer> soldItemSlots = applicator.getMask().selection(".").getSlots();
        for (Integer soldItemSlot : soldItemSlots) {
            ItemStack item = menu.getInventory().getItem(soldItemSlot);

            if (item == null) {
                continue;
            }

            itemsSold += item.getAmount();
        }
    }
}


