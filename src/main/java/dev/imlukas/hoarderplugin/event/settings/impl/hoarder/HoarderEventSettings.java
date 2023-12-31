package dev.imlukas.hoarderplugin.event.settings.impl.hoarder;

import dev.imlukas.hoarderplugin.event.data.hoarder.item.HoarderItem;
import dev.imlukas.hoarderplugin.event.settings.EventSettings;
import dev.imlukas.hoarderplugin.utils.collection.ListUtils;
import dev.imlukas.hoarderplugin.utils.time.Time;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HoarderEventSettings extends EventSettings {

    private final List<HoarderItem> whitelistedItems = new ArrayList<>();

    private boolean randomMaterial;
    private Time startingTime, eventTime;
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

    public HoarderItem getRandomItem() {
        return ListUtils.getRandom(whitelistedItems);
    }

    public void setEventTime(Time eventTime) {
        this.eventTime = eventTime;
    }

    public void setRandomMaterial(boolean randomMaterial) {
        this.randomMaterial = randomMaterial;
    }

    public void setStartingTime(Time startingTime) {
        this.startingTime = startingTime;
    }
}
