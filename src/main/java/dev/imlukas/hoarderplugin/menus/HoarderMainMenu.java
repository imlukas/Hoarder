package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.schedulerutil.ScheduledTask;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.time.localdate.DateUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class HoarderMainMenu extends UpdatableMenu {

    private final Messages messages;
    private final EventTracker eventTracker;
    private ScheduledTask scheduledTask;

    private final PlayerStats playerStats;
    private ConfigurableMenu menu;
    private String timeLeft = "";

    public HoarderMainMenu(HoarderPlugin plugin, Player viewer) {
        super(plugin, viewer);
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
                new Placeholder<>("player_name", getViewer().getName()),
                new Placeholder<>("totalWins", String.valueOf(playerStats.getWins())),
                new Placeholder<>("totalSold", String.valueOf(playerStats.getSoldItems())),
                new Placeholder<>("totalTop3", String.valueOf(playerStats.getTop3())));

        menu.setItemPlaceholders(placeholderList);
        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        menu.onClose(scheduledTask::cancel);
        ConfigurationApplicator applicator = getApplicator();
        BaseLayer layer = new BaseLayer(menu);

        applicator.registerButton(layer, "l", () -> new HoarderLeaderboardMenu(getPlugin(), getViewer()).onClose(() -> {
            setupScheduler();
            this.open();
        }));

        applicator.registerButton(layer, "h", () -> {
            Event activeEvent = eventTracker.getActiveEvent();

            if (activeEvent == null) {
                messages.sendMessage(getViewer(), "hoarder.no-event");
                return;
            }

            new HoarderSellMenu(getPlugin(), getViewer(), activeEvent).onClose(this::open).open();
        });

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
        applicator.registerButton(layer, "s");
        applicator.registerButton(layer, "c", this::close);

        menu.addRenderable(layer);
        refresh();
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
