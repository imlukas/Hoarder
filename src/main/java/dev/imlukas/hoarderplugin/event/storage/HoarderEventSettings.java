package dev.imlukas.hoarderplugin.event.storage;

import dev.imlukas.hoarderplugin.utils.collection.ListUtils;
import dev.imlukas.hoarderplugin.event.data.item.HoarderItem;
import dev.imlukas.hoarderplugin.utils.time.Time;

import java.util.ArrayList;
import java.util.List;

public class HoarderEventSettings extends EventSettings {

    private final boolean randomMaterial;
    private Time startingTime, eventTime;
    private final List<HoarderItem> whitelistedItems = new ArrayList<>();
    private HoarderItem fixedItem;

    public HoarderEventSettings(boolean randomMaterial, List<HoarderItem> whitelistedItems, Time startingTime, Time eventTime) {
        super("hoarder");
        this.randomMaterial = randomMaterial;
        this.whitelistedItems.addAll(whitelistedItems);
        this.startingTime = startingTime;
        this.eventTime = eventTime;
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

    public Time getEventTime() {
        return eventTime;
    }

    public Time getStartingTime() {
        return startingTime;
    }
}
