package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.item.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HoarderGiveSellingItemCommand implements SimpleCommand {

    private final FileConfiguration config;

    public HoarderGiveSellingItemCommand(HoarderPlugin plugin) {
        this.config = plugin.getConfig();
    }

    @Override
    public String getIdentifier() {
        return "hoarder.sellitem";
    }

    @Override
    public String getPermission() {
        return "hoarder.sellitem";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        if (!(sender instanceof Player player)) {
            return;
        }

        ItemStack sellingItem = ItemBuilder.fromSection(config.getConfigurationSection("selling-item"));
        player.getInventory().addItem(sellingItem);
    }
}
