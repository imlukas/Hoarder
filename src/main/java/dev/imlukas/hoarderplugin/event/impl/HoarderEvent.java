package dev.imlukas.hoarderplugin.event.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.HoarderEventData;
import dev.imlukas.hoarderplugin.event.data.player.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.phase.impl.EndPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.HoarderEventPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.PreStartPhase;
import dev.imlukas.hoarderplugin.event.storage.EventSettings;
import dev.imlukas.hoarderplugin.event.storage.HoarderEventSettings;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.time.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HoarderEvent extends Event {

    private final HoarderEventData eventData;
    private final HoarderEventSettings eventSettings;

    public HoarderEvent(HoarderPlugin plugin) {
        super(plugin);
        eventSettings = (HoarderEventSettings) plugin.getEventSettingsHandler().getEventSettings("hoarder");
        eventData = new HoarderEventData(eventSettings.isRandomMaterial() ? eventSettings.getRandomItem() : eventSettings.getFixedItem());

        addPhase(new PreStartPhase(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                getPlugin().getMessages().sendMessage(onlinePlayer, "hoarder.starting", new Placeholder<>("time", eventSettings.getStartingTime().toString()));
                eventData.addParticipant(new HoarderPlayerEventData(onlinePlayer.getUniqueId()));
            }
        }, eventSettings.getStartingTime()));

        addPhase(new HoarderEventPhase(this, eventSettings.getEventTime()));
        addPhase(new EndPhase(this, new Time(5, TimeUnit.SECONDS)).onEnd(() -> {
            for (HoarderPlayerEventData participant : getEventData().getParticipants()) {
                Player player = participant.getPlayer();
                getPlugin().getMessages().sendMessage(player, "hoarder.end");
            }

            Map<Integer, HoarderPlayerEventData> top = getEventData().getTop();

            for (Map.Entry<Integer, HoarderPlayerEventData> integerPlayerEventDataEntry : top.entrySet()) {
                int pos = integerPlayerEventDataEntry.getKey();
                HoarderPlayerEventData playerEventData = integerPlayerEventDataEntry.getValue();

                switch (pos) {
                    case 1 -> playerEventData.addAvailablePrize(getPrizeRegistry().getRandomPrizes(3));
                    case 2 -> playerEventData.addAvailablePrize(getPrizeRegistry().getRandomPrizes(2));
                    case 3 -> playerEventData.addAvailablePrize(getPrizeRegistry().getRandomPrizes(1));
                }
            }

            HoarderPlayerEventData top1Data = top.get(1);

            Map<String, Object> values = Map.of(
                    "winnerid", top1Data.getPlayerId().toString(),
                    "item", getEventData().getActiveItem().getMaterial().toString(),
                    "sold", top1Data.getSoldItems());

            getPlugin().getSQLHandler().insertValue(values).thenRun(() -> {
                eventTracker.getActiveEvent().end();
            });
        }));

        start();
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
