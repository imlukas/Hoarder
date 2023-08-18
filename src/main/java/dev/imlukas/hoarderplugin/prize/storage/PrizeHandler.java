package dev.imlukas.hoarderplugin.prize.storage;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.utils.item.ItemSerializer;
import dev.imlukas.hoarderplugin.utils.storage.YMLBase;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PrizeHandler extends YMLBase {
    private final PrizeRegistry prizeRegistry;

    public PrizeHandler(HoarderPlugin plugin) {
        super(plugin, "prizes.yml");
        prizeRegistry = plugin.getPrizeRegistry();

        load();
    }

    @SneakyThrows
    public void load() {
        for (String key : getConfiguration().getKeys(false)) {
            String displayName = TextUtils.color(getConfiguration().getString(key + ".display-name"));
            ItemStack displayItem = ItemSerializer.itemStackArrayFromBase64(getConfiguration().getString(key + ".display-item"))[0];
            prizeRegistry.registerPrize(new EventPrize(UUID.fromString(key), displayName, displayItem, false));
        }

        System.out.println("Loaded " + prizeRegistry.getPrizes().size() + " prize(s).");
    }

    public CompletableFuture<Void> updatePrize(EventPrize eventPrize) {
        return CompletableFuture.runAsync(() -> {
            UUID prizeId = eventPrize.getPrizeId();
            ConfigurationSection section = getConfiguration().getConfigurationSection(prizeId.toString());
            setValues(eventPrize, section);

            save();
        });

    }

    public CompletableFuture<Void> createPrize(EventPrize eventPrize) {
        return CompletableFuture.runAsync(() -> {
            UUID prizeId = eventPrize.getPrizeId();
            ConfigurationSection section = getConfiguration().createSection(prizeId.toString());

            setValues(eventPrize, section);

            save();
        });
    }

    public CompletableFuture<Void> removePrize(EventPrize eventPrize) {
        return CompletableFuture.runAsync(() -> {
            UUID prizeId = eventPrize.getPrizeId();
            getConfiguration().set(prizeId.toString(), null);

            save();
        });
    }

    public void setValues(EventPrize prize, ConfigurationSection section) {
        section.set("display-name", prize.getDisplayName());
        section.set("display-item", ItemSerializer.itemStackArrayToBase64(prize.getDisplayItem()));
    }
}
