package dev.imlukas.hoarderplugin.event.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderEventData;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.phase.impl.EndPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.PreStartPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.hoarder.HoarderEventPhase;
import dev.imlukas.hoarderplugin.event.settings.EventSettings;
import dev.imlukas.hoarderplugin.event.settings.impl.hoarder.HoarderEventSettings;
import dev.imlukas.hoarderplugin.prize.PrizeRewarder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.time.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class HoarderEvent extends Event {


    private final Messages messages;
    private final PrizeRewarder prizeRewarder;
    private final HoarderEventData eventData;
    private final HoarderEventSettings eventSettings;


    public HoarderEvent(HoarderPlugin plugin) {
        super(plugin);
        this.messages = plugin.getMessages();
        this.prizeRewarder = plugin.getPrizeRewarder();
        this.eventSettings = (HoarderEventSettings) plugin.getEventSettingsRegistry().get("hoarder");
        this.eventData = new HoarderEventData(eventSettings.isRandomMaterial() ? eventSettings.getRandomItem() : eventSettings.getFixedItem());

        addPhase(new PreStartPhase(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                messages.sendMessage(onlinePlayer, "hoarder.starting", new Placeholder<>("time", eventSettings.getStartingTime().toString()));
            }

        }, eventSettings.getStartingTime()));

        addPhase(new HoarderEventPhase(this, eventSettings.getEventTime()));
        addPhase(new EndPhase(this, this::end, new Time(1, TimeUnit.SECONDS)).onEnd(() -> {
            for (HoarderPlayerEventData participant : getEventData().getParticipants()) {
                Player player = participant.getPlayer();
                messages.sendMessage(player, "hoarder.end");
            }

            Map<Integer, HoarderPlayerEventData> top = getEventData().getTop();

            for (Map.Entry<Integer, HoarderPlayerEventData> topPlayers : top.entrySet()) {
                int pos = topPlayers.getKey();
                HoarderPlayerEventData playerEventData = topPlayers.getValue();

                playerEventData.addAvailablePrize(prizeRewarder.getReward(pos));
                messages.sendMessage(playerEventData.getPlayer(), "prize.available");
            }

            HoarderPlayerEventData playerData = top.get(ThreadLocalRandom.current().nextInt(4, top.size() + 1));
            playerData.addAvailablePrize(prizeRewarder.getReward());
            messages.sendMessage(playerData.getPlayer(), "prize.available");

            HoarderPlayerEventData top1Data = top.get(1);

            Map<String, Object> values = Map.of(
                    "winnerid", top1Data.getPlayerId().toString(),
                    "item", getEventData().getActiveItem().getMaterial().toString(),
                    "sold", top1Data.getSoldItems());

            getPlugin().getSQLHandler().insertValue(values);
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
