package dev.imlukas.hoarderplugin.listener;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderEventData;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.utils.item.ItemBuilder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RightClickChestListener implements Listener {
    private final Messages messages;
    private final Economy economy;
    private final EventTracker eventTracker;
    private final FileConfiguration config;

    public RightClickChestListener(HoarderPlugin plugin) {
        this.messages = plugin.getMessages();
        this.economy = plugin.getEconomy();
        this.config = plugin.getConfig();
        this.eventTracker = plugin.getEventTracker();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();
        String type = config.getString("selling-item.type");
        ItemStack sellingItem = ItemBuilder.fromSection(config.getConfigurationSection("selling-item"));

        if (type == null || item == null || block == null) {
            return;
        }

        if (type.equalsIgnoreCase("custom")) {
            if (!isCustom(item, sellingItem)) {
                return;
            }

        } else if (type.equalsIgnoreCase("vanilla")) {
            if (!isVanilla(item)) {
                return;
            }
        } else {
            if (isVanilla(item) || isCustom(item, sellingItem)) {
                return;
            }
        }

        if (!(block instanceof Container container)) {
            return;
        }

        if (eventTracker.getActiveEvent() == null) {
            return;
        }

        HoarderEvent hoarderEvent = (HoarderEvent) eventTracker.getActiveEvent();
        HoarderEventData eventData = hoarderEvent.getEventData();
        HoarderPlayerEventData playerData = eventData.getPlayerData(player.getUniqueId());

        if (playerData == null) {
            playerData = eventData.addParticipant(player);
        }

        Material itemMaterial = eventData.getActiveItem().getMaterial();
        double itemValue = eventData.getActiveItem().getValue();

        Inventory inventory = container.getInventory();

        int itemsSold = 0;
        for (ItemStack content : inventory.getContents()) {
            if (content == null) {
                continue;
            }

            if (content.getType() != itemMaterial) {
                continue;
            }

            playerData.addSoldItem(content.getAmount());
            itemsSold += content.getAmount();
            inventory.remove(content);
        }

        if (itemsSold == 0) {
            return;
        }

        double totalValue = itemValue * itemsSold;

        messages.sendMessage(player, "sold.success",
                new Placeholder<>("amountSold", String.format("%,d", itemsSold)),
                new Placeholder<>("soldTotal", String.format("%4.1f", totalValue)));


        economy.depositPlayer(player, totalValue);
        event.setCancelled(true);
    }

    public boolean isCustom(ItemStack hand, ItemStack sellingItem) {
        return hand.isSimilar(sellingItem);
    }

    public boolean isVanilla(ItemStack hand) {
        Material material = Material.valueOf(config.getString("selling-item.material").toUpperCase());
        return hand.getType().equals(material);
    }


}
