package dev.imlukas.hoarderplugin.event.impl;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderEventData;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.phase.impl.EndPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.PreStartPhase;
import dev.imlukas.hoarderplugin.event.phase.impl.hoarder.HoarderEventPhase;
import dev.imlukas.hoarderplugin.event.settings.impl.hoarder.HoarderEventSettings;
import dev.imlukas.hoarderplugin.leaderboard.LeaderboardCache;
import dev.imlukas.hoarderplugin.prize.EventPrize;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class HoarderEvent extends Event {



    private final Messages messages;
    private final SQLDatabase sqlDatabase;
    private final LeaderboardCache leaderboardCache;
    private final PlayerStatsRegistry playerStatsRegistry;
    private final PrizeRewarder prizeRewarder;
    @Getter
    private final HoarderEventData eventData;
    private final HoarderEventSettings eventSettings;


    public HoarderEvent(HoarderPlugin plugin) {
        super(plugin);

        this.messages = plugin.getMessages();
        this.sqlDatabase = plugin.getSqlDatabase();
        this.leaderboardCache = plugin.getLeaderboardCache();
        this.playerStatsRegistry = plugin.getPlayerStatsRegistry();
        this.prizeRewarder = plugin.getPrizeRewarder();
        this.eventSettings = plugin.getEventSettingsRegistry().get("hoarder");
        this.eventData = new HoarderEventData(eventSettings.isRandomMaterial() ? eventSettings.getRandomItem() : eventSettings.getFixedItem());

        addPhase(new PreStartPhase(this, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                messages.sendMessage(onlinePlayer, "hoarder.starting", new Placeholder<>("time", eventSettings.getStartingTime().toString()));
            }
        }, eventSettings.getStartingTime()));

        addPhase(new HoarderEventPhase(this, eventSettings.getEventTime()));
        addPhase(new EndPhase(this, new Time(1, TimeUnit.SECONDS)).onEnd(() -> {
            this.end();
            List<HoarderPlayerEventData> participants = eventData.getParticipants();

            if (participants.isEmpty()) {
                return;
            }

            Map<Integer, HoarderPlayerEventData> leaderboard = eventData.getLeaderboard();
            Map<Integer, HoarderPlayerEventData> top3 = eventData.getTop3();

            for (HoarderPlayerEventData participant : participants) {

                Player player = participant.getPlayer();
                UUID playerId = participant.getPlayerId();

                PlayerStats playerStats = playerStatsRegistry.getPlayerStats(playerId);
                updatePlayerStats(playerStats, participant, top3);

                if (player != null) {
                    sendEndingMessages(player, leaderboard);
                }
            }

            for (int i = 1; i < 5; i++) {
                int position = i == 4 ? ThreadLocalRandom.current().nextInt(4, leaderboard.size() + 1) : i;

                HoarderPlayerEventData playerEventData = leaderboard.get(position);
                rewardPlayer(playerEventData, position);
            }

            insertWinnerValues(leaderboard);
            leaderboardCache.update(playerStatsRegistry.getPlayerStatsMap());
        }));

        start();
        plugin.setupScheduler(
                getEventSettings().getEventTime().as(TimeUnit.SECONDS) +
                getEventSettings().getStartingTime().as(TimeUnit.SECONDS) +
                5);
    }

    public void sendEndingMessages(Player player, Map<Integer, HoarderPlayerEventData> leaderboard) {
        int iterationAmount = Math.min(leaderboard.size(), 4);
        messages.sendMessage(player, "hoarder.end-header");
        for (int i = 1; i <= iterationAmount; i++) {
            HoarderPlayerEventData playerEventData = leaderboard.get(i);
            String playerName = playerEventData.getPlayerName();

            messages.sendMessage(player, "hoarder.end-entry",
                    new Placeholder<>("pos", String.valueOf(i)),
                    new Placeholder<>("items", String.valueOf(playerEventData.getSoldItems())),
                    new Placeholder<>("player", playerName));
        }
    }

    public void rewardPlayer(HoarderPlayerEventData playerEventData, int position) {
        List<EventPrize> prizes = prizeRewarder.getReward(position);
        playerEventData.addPrizes(prizes);
        messages.sendMessage(playerEventData.getPlayer(), "prize.available");
    }

    public void updatePlayerStats(PlayerStats playerStats, HoarderPlayerEventData playerEventData, Map<Integer, HoarderPlayerEventData> top3) {
        UUID playerId = playerEventData.getPlayerId();

        if (top3.containsValue(playerEventData)) {
            playerStats.addTop3();
        }

        if (top3.get(1).getPlayerId().equals(playerId)) {
            playerStats.addWin();
        }

        playerStats.addSoldItems(playerEventData.getSoldItems());
    }

    public void insertWinnerValues(Map<Integer, HoarderPlayerEventData> leaderboard) {
        Map<String, Object> sqlValues = new HashMap<>();

        int iterationAmount = Math.min(leaderboard.size(), 4);
        for (int i = 1; i <= iterationAmount; i++) {
            sqlValues.put("top" + i, leaderboard.get(i).getPlayerId().toString());
        }

        sqlValues.put("item", getEventData().getActiveItem().getMaterial().toString());
        sqlValues.put("top1_sold", leaderboard.get(1).getSoldItems());

        sqlDatabase.getOrCreateTable(SQLTableType.HOARDER_WINNER.getName()).insert(sqlValues);
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
