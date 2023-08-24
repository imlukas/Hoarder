package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderEventData;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HoarderSellMenu extends UpdatableMenu {

    private final Messages messages;
    private final Economy economy;
    private final HoarderEvent activeEvent;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private BaseLayer layer;

    private int itemsSold;

    public HoarderSellMenu(HoarderPlugin plugin, Player viewer, @NotNull Event activeEvent) {
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
        onClose(this::giveItemsBack);

        applicator.registerButton(layer, "c", this::close);
        applicator.registerButton(layer, "s", this::sellItemsInMenu);
        applicator.registerButton(layer, "sa", this::sellInventory);

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
                new Placeholder<>("amountSold", playerData == null ? "0" : String.valueOf(playerData.getSoldItems())),
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


    public void handleSell() {
        if (itemsSold == 0) {
            messages.sendMessage(getViewer(), "sold.fail");
            return;
        }

        HoarderPlayerEventData playerData = activeEvent.getEventData().getPlayerData(getViewer().getUniqueId());

        if (playerData == null) {
            playerData = activeEvent.getEventData().addParticipant(getViewer());
        }

        playerData.addSoldItem(itemsSold);
        double currencyAmount = activeEvent.getEventData().getActiveItem().getValue() * itemsSold;

        messages.sendMessage(getViewer(), "sold.success",
                new Placeholder<>("amountSold", String.format("%,d", itemsSold)),
                new Placeholder<>("soldTotal", String.format("%4.1f", currencyAmount)));

        economy.depositPlayer(getViewer(), currencyAmount);
        refresh();
    }

    public void giveItemsBack() {
        List<Integer> soldItemSlots = applicator.getMask().selection(".").getSlots();
        for (Integer soldItemSlot : soldItemSlots) {
            ItemStack item = menu.getInventory().getItem(soldItemSlot);

            if (item == null) {
                continue;
            }

            getViewer().getInventory().addItem(item);
        }
    }

    public List<ItemStack> getValidItems(Inventory inventory) {
        List<ItemStack> validItems = new ArrayList<>();
        Material activeItem = activeEvent.getEventData().getActiveItem().getMaterial();

        for (ItemStack storageContent : inventory.getStorageContents()) {
            if (storageContent == null || storageContent.getType().isAir()) {
                return validItems;
            }

            if (storageContent.getType() != activeItem) {
                continue;
            }

            validItems.add(storageContent);
        }

        return validItems;
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

    private void sellItemsInMenu() {
        getValidItems(getViewer().getInventory()).forEach(item -> itemsSold += item.getAmount());
        handleSell();
    }

    private void sellInventory() {
        Player player = getViewer();
        Inventory inventory = player.getInventory();
        getValidItems(inventory).forEach(item -> itemsSold += item.getAmount());
        handleSell();
    }
}


