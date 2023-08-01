package dev.imlukas.hoarderplugin;

import dev.imlukas.hoarderplugin.command.*;
import dev.imlukas.hoarderplugin.command.editor.PrizesCommand;
import dev.imlukas.hoarderplugin.command.editor.SettingsCommand;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.event.settings.handler.EventSettingsHandler;
import dev.imlukas.hoarderplugin.event.settings.registry.EventSettingsRegistry;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import dev.imlukas.hoarderplugin.items.handler.CustomItemHandler;
import dev.imlukas.hoarderplugin.items.registry.CustomItemRegistry;
import dev.imlukas.hoarderplugin.listener.DisconnectListener;
import dev.imlukas.hoarderplugin.listener.RightClickChestListener;
import dev.imlukas.hoarderplugin.prize.PrizeRewarder;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.prize.storage.PrizeHandler;
import dev.imlukas.hoarderplugin.storage.SQLHandler;
import dev.imlukas.hoarderplugin.storage.sql.SQLDatabase;
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
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class HoarderPlugin extends JavaPlugin {

    private CommandManager commandManager;
    private MenuRegistry menuRegistry;
    private Messages messages;
    private Economy economy;
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

    private ScheduledTask scheduledTask;


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

        registerCommand(new HoarderCommand(this));
        registerCommand(new HoarderForceStartCommand(this));
        registerCommand(new HoarderForceEndCommand(this));
        registerCommand(new HoarderInfoCommand(this));
        registerCommand(new HoarderRewardsCommand(this));
        registerCommand(new HoarderGiveSellingItemCommand(this));

        commandManager.registerCommand(new PrizesCommand(this));
        commandManager.registerCommand(new SettingsCommand(this));
        registerListener(new RightClickChestListener(this));
        registerListener(new DisconnectListener(this));

        setupHoarderTimer();
    }


    public void setupHoarderTimer() {
        long lastEpoch = getConfig().getLong("time-remaining.value");
        if (lastEpoch != 0) {
            long timeFromShutdown = (System.currentTimeMillis() - lastEpoch) / 1000;
            long twelveHours = 3600 * 12;

            long timeRemaining = twelveHours - (timeFromShutdown % twelveHours);

            System.out.println("[Hoarder] Event will start in " + timeRemaining / 3600 + "hours");
            scheduledTask = new ScheduleBuilder(this).in(timeRemaining).seconds().run(() -> {
                new HoarderEvent(this);
                setupScheduler();
            }).sync().start().onCancel(this::storeTime);
        } else {
            setupScheduler();
        }
    }

    public void setupScheduler() {
        scheduledTask = new ScheduleBuilder(this).every(12).hours().run(() -> {
            new HoarderEvent(this);
        }).sync().start().onCancel(this::storeTime);
    }

    public void storeTime() {
        boolean timeEnabled = getConfig().getBoolean("time-remaining.enabled");
        if (timeEnabled) {
            getConfig().set("time-remaining.value", System.currentTimeMillis());
            saveConfig();
        }
    }

    @Override
    public void onDisable() {
        scheduledTask.cancel();
    }

    public void initSQL() {
        ColumnData winnerId = new ColumnData("winnerid", ColumnType.VARCHAR, 36);
        ColumnData sold = new ColumnData("sold", ColumnType.INT);
        ColumnData itemMaterial = new ColumnData("item", ColumnType.VARCHAR, 36);

        sqlDatabase.getOrCreateTable("hoarder").addColumn(winnerId, sold, itemMaterial);
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
