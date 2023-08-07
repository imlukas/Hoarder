package dev.imlukas.hoarderplugin;

import dev.imlukas.hoarderplugin.command.*;
import dev.imlukas.hoarderplugin.command.editor.PrizesCommand;
import dev.imlukas.hoarderplugin.command.editor.SettingsCommand;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.event.settings.handler.EventSettingsHandler;
import dev.imlukas.hoarderplugin.event.settings.registry.EventSettingsRegistry;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.hooks.HoarderPlaceholderExtension;
import dev.imlukas.hoarderplugin.items.handler.CustomItemHandler;
import dev.imlukas.hoarderplugin.items.registry.CustomItemRegistry;
import dev.imlukas.hoarderplugin.leaderboard.LeaderboardCache;
import dev.imlukas.hoarderplugin.listener.ConnectionListener;
import dev.imlukas.hoarderplugin.listener.RightClickChestListener;
import dev.imlukas.hoarderplugin.prize.PrizeRewarder;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.prize.storage.PrizeHandler;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStatsRegistry;
import dev.imlukas.hoarderplugin.storage.sql.SQLDatabase;
import dev.imlukas.hoarderplugin.storage.sql.SQLHandler;
import dev.imlukas.hoarderplugin.storage.sql.constants.ColumnType;
import dev.imlukas.hoarderplugin.storage.sql.data.ColumnData;
import dev.imlukas.hoarderplugin.utils.command.command.CommandManager;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.concurrency.MainThreadExecutor;
import dev.imlukas.hoarderplugin.utils.io.FileUtils;
import dev.imlukas.hoarderplugin.utils.menu.registry.MenuRegistry;
import dev.imlukas.hoarderplugin.utils.schedulerutil.ScheduledTask;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public final class HoarderPlugin extends JavaPlugin {

    private CommandManager commandManager;
    private MenuRegistry menuRegistry;
    private Messages messages;
    private Economy economy;

    private PlayerStatsRegistry playerStatsRegistry;

    private SQLDatabase sqlDatabase;
    private SQLHandler SQLHandler;

    private EventRegistry eventRegistry;
    private EventTracker eventTracker;
    private EventSettingsRegistry eventSettingsRegistry;
    private EventSettingsHandler eventSettingsHandler;

    private ActionRegistry actionRegistry;

    private CustomItemRegistry customItemRegistry;
    private CustomItemHandler customItemHandler;

    private PrizeRegistry prizeRegistry;
    private PrizeHandler prizeHandler;
    private PrizeRewarder prizeRewarder;

    private LeaderboardCache leaderboardCache;

    private ScheduledTask scheduledTask;
    private LocalDateTime timeLeft;


    @Override
    public void onEnable() {
        System.out.println("[HoarderPlugin] Enabling HoarderPlugin v" + getDescription().getVersion() + " by imlukas.");
        saveDefaultConfig();

        MainThreadExecutor.init(this);
        FileUtils.copyBuiltInResources(this, getFile());
        messages = new Messages(this);
        commandManager = new CommandManager(this);
        menuRegistry = new MenuRegistry(this);
        setupEconomy();

        playerStatsRegistry = new PlayerStatsRegistry();

        sqlDatabase = new SQLDatabase(this.getConfig().getConfigurationSection("mysql"));
        SQLHandler = new SQLHandler(this);
        initSQL();

        eventRegistry = new EventRegistry(this);
        eventRegistry.registerEvent("hoarder", HoarderEvent::new);

        eventTracker = new EventTracker();
        eventSettingsRegistry = new EventSettingsRegistry();
        eventSettingsHandler = new EventSettingsHandler(this);

        actionRegistry = new ActionRegistry(this);

        customItemRegistry = new CustomItemRegistry();
        customItemHandler = new CustomItemHandler(this);

        prizeRegistry = new PrizeRegistry();
        prizeHandler = new PrizeHandler(this);
        prizeRewarder = new PrizeRewarder(this);

        leaderboardCache = new LeaderboardCache(this);

        new ScheduleBuilder(this).every(30).seconds().run(() -> {
            SQLHandler.fetchEventStats().thenAccept(eventStats -> leaderboardCache.update(eventStats));
        }).sync().start();

        registerCommand(new HoarderSellCommand(this));
        registerCommand(new HoarderForceStartCommand(this));
        registerCommand(new HoarderForceEndCommand(this));
        registerCommand(new HoarderInfoCommand(this));
        registerCommand(new HoarderRewardsCommand(this));
        registerCommand(new HoarderGiveSellingItemCommand(this));
        registerCommand(new HoarderLeaderboardCommand(this));
        commandManager.registerCommand(new ReloadCommand(this));

        commandManager.registerCommand(new PrizesCommand(this));
        commandManager.registerCommand(new SettingsCommand(this));
        commandManager.registerCommand(new HoarderCommand(this));

        registerListener(new RightClickChestListener(this));
        registerListener(new ConnectionListener(this));

        new HoarderPlaceholderExtension(this).register();
        setupHoarderTimer();
    }

    @Override
    public void onDisable() {
        scheduledTask.cancel();
    }

    public void reload() {
        messages = new Messages(this);
        actionRegistry = new ActionRegistry(this);

        customItemRegistry = new CustomItemRegistry();
        customItemHandler = new CustomItemHandler(this);

        prizeRegistry = new PrizeRegistry();
        prizeHandler = new PrizeHandler(this);
        prizeRewarder = new PrizeRewarder(this);

        menuRegistry = new MenuRegistry(this);
    }

    public void setupHoarderTimer() {
        long lastEpoch = getConfig().getLong("time-remaining.value");

        if (lastEpoch != 0) {
            long timeFromShutdown = (System.currentTimeMillis() - lastEpoch) / 1000;
            long twelveHours = 3600 * 12;
            long timeRemaining = twelveHours - (timeFromShutdown % twelveHours);

            updateTimeLeft(timeRemaining);
            setupScheduler(timeRemaining);
            return;
        }
        new HoarderEvent(this);
    }

    public void updateTimeLeft(long timeToStart) {
        LocalDateTime time = LocalDateTime.now();
        timeLeft = time.plusSeconds(timeToStart);
    }

    public void setupScheduler(long timeLeft) {
        updateTimeLeft(timeLeft);
        final long[] finalTimeLeft = {timeLeft};
        scheduledTask = new ScheduleBuilder(this).every(1).seconds().run(() -> {
            finalTimeLeft[0]--;
            if (finalTimeLeft[0] == 0) {
                new HoarderEvent(this);
            }
        }).sync().start().onCancel(this::storeTime);
    }

    public void storeTime() {
        boolean timeEnabled = getConfig().getBoolean("time-remaining.enabled");
        if (timeEnabled) {
            getConfig().set("time-remaining.value", System.currentTimeMillis());
            saveConfig();
        }
    }

    public Duration getTimeLeft() {
        return Duration.between(LocalDateTime.now(), timeLeft);
    }

    public void initSQL() {
        ColumnData top1 = new ColumnData("top1", ColumnType.VARCHAR, 36);
        ColumnData top2 = new ColumnData("top2", ColumnType.VARCHAR, 36);
        ColumnData top3 = new ColumnData("top3", ColumnType.VARCHAR, 36);
        ColumnData top1Sold = new ColumnData("top1_sold", ColumnType.INT);
        ColumnData itemMaterial = new ColumnData("item", ColumnType.TINYTEXT);

        sqlDatabase.getOrCreateTable("hoarder_winners").addColumn(top1, top2, top3, top1Sold, itemMaterial);

        ColumnData sold = new ColumnData("sold", ColumnType.INT);
        ColumnData wins = new ColumnData("wins", ColumnType.INT);
        ColumnData top3s = new ColumnData("top_3", ColumnType.INT);

        sqlDatabase.createTable("hoarder_stats",
                "CREATE TABLE IF NOT EXISTS hoarder_stats (player_id VARCHAR(36) NOT NULL, PRIMARY KEY(player_id));").addColumn(sold, wins, top3s);
    }

    public void registerCommand(SimpleCommand command) {
        commandManager.registerCommand(command);
    }

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return true;
    }
}
