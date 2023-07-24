package dev.imlukas.hoarderplugin.event.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.HoarderEventData;
import dev.imlukas.hoarderplugin.event.data.player.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.data.player.PlayerEventData;
import dev.imlukas.hoarderplugin.event.phase.impl.EndPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.HoarderEventPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.PreStartPhase;
import dev.imlukas.hoarderplugin.event.storage.EventSettings;
import dev.imlukas.hoarderplugin.event.storage.HoarderEventSettings;
import dev.imlukas.hoarderplugin.utils.time.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HoarderEvent extends Event{

    private final HoarderEventData eventData;
    private final HoarderEventSettings eventSettings;

    public HoarderEvent(HoarderPlugin plugin) {
        super(plugin);
        eventSettings = (HoarderEventSettings) plugin.getEventSettingsHandler().getEventSettings("hoarder");
        eventData = new HoarderEventData(eventSettings.isRandomMaterial() ? eventSettings.getRandomItem() : eventSettings.getFixedItem());

        addPhase(new PreStartPhase(this, () -> {
            plugin.getServer().broadcastMessage("The Hoarder Event is Starting");

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                eventData.addParticipant(onlinePlayer);
            }

        }, new Time(15, TimeUnit.SECONDS)));

        addPhase(new HoarderEventPhase(this, new Time(10, TimeUnit.MINUTES)));
        addPhase(new EndPhase(this, () -> {

            for (Map.Entry<HoarderPlayerEventData, Integer> integerPlayerEventDataEntry : eventData.getTop().entrySet()) {
                HoarderPlayerEventData playerEventData = integerPlayerEventDataEntry.getKey();
                int pos = integerPlayerEventDataEntry.getValue();
            }


        }, new Time(5, TimeUnit.SECONDS)));
    }

    public HoarderEventData getEventData() {
        return eventData;
    }

    @Override
    public EventSettings getEventSettings() {
        return eventSettings;
    }

    @Override
    public String getIdentifier() {
        return "hoarder";
    }
}
