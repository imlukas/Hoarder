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
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStatsRegistry;
import dev.imlukas.hoarderplugin.storage.sql.SQLDatabase;
import dev.imlukas.hoarderplugin.storage.sql.SQLTableType;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.time.Time;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class HoarderEvent extends Event {


    private final Messages messages;
    private final SQLDatabase sqlDatabase;
    private final PlayerStatsRegistry playerStatsRegistry;
    private final PrizeRewarder prizeRewarder;
    @Getter
    private final HoarderEventData eventData;
    private final HoarderEventSettings eventSettings;


    public HoarderEvent(HoarderPlugin plugin) {
        super(plugin);

        this.messages = plugin.getMessages();
        this.sqlDatabase = plugin.getSqlDatabase();
        this.playerStatsRegistry = plugin.getPlayerStatsRegistry();
        this.prizeRewarder = plugin.getPrizeRewarder();
        this.eventSettings = (HoarderEventSettings) plugin.getEventSettingsRegistry().get("hoarder");
        this.eventData = new HoarderEventData(eventSettings.isRandomMaterial() ? eventSettings.getRandomItem() : eventSettings.getFixedItem());

        addPhase(new PreStartPhase(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                messages.sendMessage(onlinePlayer, "hoarder.starting", new Placeholder<>("time", eventSettings.getStartingTime().toString()));
            }

        }, eventSettings.getStartingTime()));

        addPhase(new HoarderEventPhase(this, eventSettings.getEventTime()));
        addPhase(new EndPhase(this, new Time(1, TimeUnit.SECONDS)).onEnd(() -> {
            this.end();
            Map<Integer, HoarderPlayerEventData> top = getEventData().getTop();

            for (HoarderPlayerEventData participant : getEventData().getParticipants()) {

                Player player = participant.getPlayer();

                PlayerStats playerStats = playerStatsRegistry.getPlayerStats(player.getUniqueId());
                if (eventData.isTop3(player.getUniqueId())) {
                    playerStats.addTop3();
                }

                if (top.get(1).getPlayerId().equals(player.getUniqueId())) {
                    playerStats.addWin();
                }

                playerStats.addSoldItems(participant.getSoldItems());

                messages.sendMessage(player, "hoarder.end-header");

                int iterationAmount = Math.min(top.size(), 4);
                for (int i = 1; i <= iterationAmount; i++) {
                    messages.sendMessage(player, "hoarder.end-entry",
                            new Placeholder<>("pos", String.valueOf(i)),
                            new Placeholder<>("items", String.valueOf(top.get(i).getSoldItems())),
                            new Placeholder<>("player", top.get(i).getPlayer().getName()));
                }
            }

            for (Map.Entry<Integer, HoarderPlayerEventData> topPlayers : top.entrySet()) {
                int pos = topPlayers.getKey();
                HoarderPlayerEventData playerEventData = topPlayers.getValue();

                playerEventData.addPrizes(prizeRewarder.getReward(pos));
                messages.sendMessage(playerEventData.getPlayer(), "prize.available");
            }

            HoarderPlayerEventData randomPlayerData;

            if (top.size() >= 4) {
                randomPlayerData = top.get(ThreadLocalRandom.current().nextInt(4, top.size() + 1));
                randomPlayerData.addPrizes(prizeRewarder.getReward());
                messages.sendMessage(randomPlayerData.getPlayer(), "prize.available");
            }

            Map<String, Object> winnerValues = new HashMap<>();

            if (top.size() <= 3) {
                for (int i = 1; i <= top.size(); i++) {
                    winnerValues.put("top" + i, top.get(i).getPlayerId().toString());
                }
            } else {
                for (int i = 1; i < 4; i++) {
                    winnerValues.put("top" + i, top.get(i).getPlayerId().toString());
                }
            }

            winnerValues.put("item", getEventData().getActiveItem().getMaterial().toString());
            winnerValues.put("top1_sold", top.get(1).getSoldItems());

            sqlDatabase.getOrCreateTable(SQLTableType.HOARDER_WINNER.getName()).insert(winnerValues);
        }));

        start();
        plugin.setupScheduler(
                getEventSettings().getEventTime().as(TimeUnit.SECONDS) +
                getEventSettings().getStartingTime().as(TimeUnit.SECONDS) +
                5);
    }

    @Override
    public HoarderEventSettings getEventSettings() {
        return eventSettings;
    }

    @Override
    public String getIdentifier() {
        return "hoarder";
    }
}
