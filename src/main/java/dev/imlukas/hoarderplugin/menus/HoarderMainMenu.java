package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderEventData;
import dev.imlukas.hoarderplugin.event.data.hoarder.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.leaderboard.LeaderboardCache;
import dev.imlukas.hoarderplugin.menus.editors.prize.PrizeListMenu;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.button.DecorationItem;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.schedulerutil.ScheduledTask;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.time.localdate.DateUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HoarderMainMenu extends UpdatableMenu {

    private final LeaderboardCache leaderboardCache;
    private final Messages messages;
    private final EventTracker eventTracker;
    private ScheduledTask scheduledTask;

    private final PlayerStats playerStats;
    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private BaseLayer layer;
    private PaginableArea area;
    private Button hoarderButton;
    private String timeLeft = "";


    public HoarderMainMenu(HoarderPlugin plugin, Player viewer) {
        super(plugin, viewer);
        this.leaderboardCache = plugin.getLeaderboardCache();
        this.messages = plugin.getMessages();
        this.eventTracker = plugin.getEventTracker();
        this.playerStats = plugin.getPlayerStatsRegistry().getPlayerStats(viewer.getUniqueId());
        scheduledTask = new ScheduleBuilder(plugin).every(1).seconds().run(() -> {
            timeLeft = DateUtil.formatDuration(plugin.getTimeLeft());

            if (getViewer() == null) {
                scheduledTask.cancel();
                return;
            }

            refresh();
        }).sync().start();
        setup();
    }

    @Override
    public void refresh() {
        if (getViewer() == null) {
            return;
        }

        area.clear();

        HoarderEvent activeEvent = (HoarderEvent) eventTracker.getActiveEvent();
        HoarderEventData eventData = activeEvent == null ? null : activeEvent.getEventData();

        List<Placeholder<Player>> generalPlaceholders = new ArrayList<>();
        generalPlaceholders.add(new Placeholder<>("time", timeLeft));
        generalPlaceholders.add(new Placeholder<>("status", activeEvent == null ? "inactive" : "active"));
        generalPlaceholders.add(new Placeholder<>("statsName", getViewer().getName()));
        generalPlaceholders.add(new Placeholder<>("statsWins", String.valueOf(playerStats.getWins())));
        generalPlaceholders.add(new Placeholder<>("statsSold", String.valueOf(playerStats.getSoldItems())));
        generalPlaceholders.add(new Placeholder<>("statsTop3", String.valueOf(playerStats.getTop3())));

        layer.setItemPlaceholders(generalPlaceholders);

        if (activeEvent == null) {
            menu.forceUpdate();
            return;
        }

        Map<Integer, HoarderPlayerEventData> leaderboard = eventData.getLeaderboard();
        HoarderPlayerEventData viewerData = eventData.getPlayerData(getViewerId());

        if (viewerData == null) {
            generalPlaceholders.add(new Placeholder<>("statsPosition", "N/A"));
            generalPlaceholders.add(new Placeholder<>("statsCurrentSold", "N/A"));
        } else {
            for (Map.Entry<Integer, HoarderPlayerEventData> leaderboardEntry : leaderboard.entrySet()) {
                HoarderPlayerEventData playerData = leaderboardEntry.getValue();
                int position = leaderboardEntry.getKey();
                UUID playerId = playerData.getPlayerId();

                if (playerId.equals(getViewerId())) {
                    generalPlaceholders.add(new Placeholder<>("statsPosition", String.valueOf(position)));
                    generalPlaceholders.add(new Placeholder<>("statsCurrentSold", String.valueOf(playerData.getSoldItems())));
                    break;
                }
            }
        }

        int iterationAmount = Math.min(leaderboard.size(), 3);
        for (int i = 1; i <= iterationAmount; i++) {

            HoarderPlayerEventData playerData = leaderboard.get(i);
            List<Placeholder<Player>> placeholderList = getPlaceholders(playerData, i);

            DecorationItem decorationItem = new DecorationItem(applicator.getItem("t"));
            decorationItem.setItemPlaceholders(placeholderList);
            area.addElement(decorationItem);
        }

        hoarderButton.getDisplayItem().setType(eventData.getActiveItem().getMaterial());
        layer.setItemPlaceholders(generalPlaceholders);
        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        menu.onClose(scheduledTask::cancel);

        applicator = menu.getApplicator();

        HoarderEvent activeEvent = (HoarderEvent) eventTracker.getActiveEvent();
        ConfigurationApplicator applicator = getApplicator();
        area = new PaginableArea(applicator.getMask().selection("t"));
        area.setEmptyElement(new DecorationItem(applicator.getItem("empty")));
        PaginableLayer paginableLayer = new PaginableLayer(menu, area);
        layer = new BaseLayer(menu);

        applicator.registerButton(layer, "l", () -> new HoarderLeaderboardMenu(getPlugin(), getViewer()).onClose(() -> {
            setupScheduler();
            this.open();
        }));

        hoarderButton = applicator.registerButton(layer, "h", () -> {
            HoarderEvent event = (HoarderEvent) eventTracker.getActiveEvent();

            if (event == null) {
                messages.sendMessage(getViewer(), "hoarder.no-event");
                return;
            }

            new HoarderSellMenu(getPlugin(), getViewer(), event).onClose(() -> {
                setupScheduler();
                open();
            }).open();
        });

        hoarderButton.getDisplayItem().setType(activeEvent == null ? Material.CHEST : activeEvent.getEventData().getActiveItem().getMaterial());

        applicator.registerButton(layer, "r", () -> {
            Event lastEvent = eventTracker.getLastEvent();

            if (lastEvent == null) {
                messages.sendMessage(getViewer(), "command.no-prizes");
                return;
            }

            new HoarderRewardsMenu(getPlugin(), getViewer(), (HoarderEvent) lastEvent).onClose(() -> {
                setupScheduler();
                this.open();
            }).open();
        });

        applicator.registerButton(layer, "p", () -> new PrizeListMenu(getPlugin(), getViewer()).onClose(() -> {
            setupScheduler();
            this.open();
        }).open());
        applicator.registerButton(layer, "s");
        applicator.registerButton(layer, "c", this::close);

        menu.addRenderable(layer, paginableLayer);
        refresh();
    }

    @NotNull
    private List<Placeholder<Player>> getPlaceholders(HoarderPlayerEventData playerData, int i) {
        PlayerStats playerStats = getPlugin().getPlayerStatsRegistry().getPlayerStats(playerData.getPlayerId());
        List<Placeholder<Player>> placeholders = new ArrayList<>();
        placeholders.add(new Placeholder<>("player_name", playerData.getPlayerName()));
        placeholders.add(new Placeholder<>("position", String.valueOf(i)));
        placeholders.add(new Placeholder<>("totalSold", String.valueOf(playerData.getSoldItems())));

        if (playerStats == null) {
            getPlugin().getSqlHandler().fetchPlayerStats(playerData.getPlayerId()).thenAccept(fetchedStats -> {
                if (fetchedStats == null) {
                    return;
                }
                placeholders.add(new Placeholder<>("totalWins", String.valueOf(fetchedStats.getWins())));
                placeholders.add(new Placeholder<>("totalSold", String.valueOf(fetchedStats.getSoldItems())));
                placeholders.add(new Placeholder<>("totalTop3", String.valueOf(fetchedStats.getTop3())));
            });

            return placeholders;
        }

        placeholders.add(new Placeholder<>("totalWins", String.valueOf(playerStats.getWins())));
        placeholders.add(new Placeholder<>("totalSold", String.valueOf(playerStats.getSoldItems())));
        placeholders.add(new Placeholder<>("totalTop3", String.valueOf(playerStats.getTop3())));
        return placeholders;
    }

    @Override
    public String getIdentifier() {
        return "hoarder-main";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }

    @Override
    public void close() {
        scheduledTask.cancel();
        super.close();
    }

    public void setupScheduler() {
        scheduledTask = new ScheduleBuilder(getPlugin()).every(1).seconds().run(() -> {
            timeLeft = DateUtil.formatDuration(getPlugin().getTimeLeft());
            refresh();
        }).sync().start();
    }
}
