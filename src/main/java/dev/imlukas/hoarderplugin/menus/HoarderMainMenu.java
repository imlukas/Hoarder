package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
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

import java.util.List;

public class HoarderMainMenu extends UpdatableMenu {

    private final LeaderboardCache leaderboardCache;
    private final Messages messages;
    private final EventTracker eventTracker;
    private ScheduledTask scheduledTask;

    private final PlayerStats playerStats;
    private ConfigurableMenu menu;
    private BaseLayer layer;
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
            refresh();
        }).sync().start();

        setup();

    }

    @Override
    public void refresh() {
        List<Placeholder<Player>> placeholderList = List.of(new Placeholder<>("time", timeLeft),
                new Placeholder<>("status", eventTracker.getActiveEvent() == null ? "inactive" : "active"),
                new Placeholder<>("statsName", getViewer().getName()),
                new Placeholder<>("statsWins", String.valueOf(playerStats.getWins())),
                new Placeholder<>("statsSold", String.valueOf(playerStats.getSoldItems())),
                new Placeholder<>("statsTop3", String.valueOf(playerStats.getTop3())));

        HoarderEvent activeEvent = (HoarderEvent) eventTracker.getActiveEvent();
        hoarderButton.getDisplayItem().setType(activeEvent == null ? Material.CHEST : activeEvent.getEventData().getActiveItem().getMaterial());
        layer.setItemPlaceholders(placeholderList);
        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        menu.onClose(scheduledTask::cancel);

        HoarderEvent activeEvent = (HoarderEvent) eventTracker.getActiveEvent();
        ConfigurationApplicator applicator = getApplicator();
        PaginableArea area = new PaginableArea(applicator.getMask().selection("t"));
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

        int i = 1;
        for (PlayerStats stats : leaderboardCache.getTop(3).values()) {

            List<Placeholder<Player>> placeholderList = getPlaceholders(stats, i);

            DecorationItem decorationItem = new DecorationItem(applicator.getItem("t"));
            decorationItem.setItemPlaceholders(placeholderList);
            area.addElement(decorationItem);
            i++;
        }

        menu.addRenderable(layer, paginableLayer);
        refresh();
    }

    @NotNull
    private static List<Placeholder<Player>> getPlaceholders(PlayerStats stats, int i) {
        String offlinePlayerName = stats.getOfflinePlayer().getName();

        return List.of(new Placeholder<>("player_name", offlinePlayerName),
                new Placeholder<>("position", String.valueOf(i)),
                new Placeholder<>("totalWins", String.valueOf(stats.getWins())),
                new Placeholder<>("totalSold", String.valueOf(stats.getSoldItems())),
                new Placeholder<>("totalTop3", String.valueOf(stats.getTop3())));
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
