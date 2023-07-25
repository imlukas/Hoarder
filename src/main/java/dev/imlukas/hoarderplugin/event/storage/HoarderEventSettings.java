package dev.imlukas.hoarderplugin.event.storage;

import dev.imlukas.hoarderplugin.utils.collection.ListUtils;
import dev.imlukas.hoarderplugin.event.data.item.HoarderItem;

import java.util.ArrayList;
import java.util.List;

public class HoarderEventSettings extends EventSettings {

    private final boolean randomMaterial;
    private final List<HoarderItem> whitelistedItems = new ArrayList<>();
    private HoarderItem fixedItem;

    public HoarderEventSettings(boolean randomMaterial, List<HoarderItem> whitelistedItems) {
        super("hoarder");
        this.randomMaterial = randomMaterial;
        this.whitelistedItems.addAll(whitelistedItems);
    }

    public void setFixedItem(HoarderItem fixedItem) {
        this.fixedItem = fixedItem;
    }

    public boolean isRandomMaterial() {
        return randomMaterial;
    }

    public List<HoarderItem> getWhitelistedItems() {
        return whitelistedItems;
    }

    public HoarderItem getFixedItem() {
        return fixedItem;
    }

    public HoarderItem getRandomItem() {
        return ListUtils.getRandom(whitelistedItems);
    }
}
