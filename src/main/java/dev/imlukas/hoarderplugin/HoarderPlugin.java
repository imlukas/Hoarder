package dev.imlukas.hoarderplugin;

import dev.imlukas.hoarderplugin.command.HoarderCommand;
import dev.imlukas.hoarderplugin.command.HoarderForceStartCommand;
import dev.imlukas.hoarderplugin.command.HoarderInfoCommand;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.event.storage.EventSettingsHandler;
import dev.imlukas.hoarderplugin.items.handler.CustomItemHandler;
import dev.imlukas.hoarderplugin.items.registry.CustomItemRegistry;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.prize.storage.PrizeHandler;
import dev.imlukas.hoarderplugin.utils.command.SimpleCommand;
import dev.imlukas.hoarderplugin.utils.command.impl.CommandManager;
import dev.imlukas.hoarderplugin.utils.io.FileUtils;
import dev.imlukas.hoarderplugin.utils.menu.registry.MenuRegistry;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class HoarderPlugin extends JavaPlugin {

    private CommandManager commandManager;
    private MenuRegistry menuRegistry;
    private Messages messages;
    private Economy economy;

    private EventRegistry eventRegistry;
    private EventSettingsHandler eventSettingsHandler;

    private ActionRegistry actionRegistry;

    private CustomItemRegistry customItemRegistry;
    private CustomItemHandler customItemHandler;

    private PrizeRegistry prizeRegistry;
    private PrizeHandler prizeHandler;


    @Override
    public void onEnable() {
        // Plugin startup logic
        FileUtils.copyBuiltInResources(this, getFile());
        commandManager = new CommandManager(this);
        menuRegistry = new MenuRegistry(this);
        messages = new Messages(this);
        setupEconomy();

        eventRegistry = new EventRegistry(this);
        eventRegistry.registerEvent("hoarder", HoarderEvent::new);

        eventSettingsHandler = new EventSettingsHandler(this);

        actionRegistry = new ActionRegistry(this);

        customItemRegistry = new CustomItemRegistry();
        customItemHandler = new CustomItemHandler(this);

        prizeRegistry = new PrizeRegistry();
        prizeHandler = new PrizeHandler(this);

        registerCommand(new HoarderCommand(this));
        registerCommand(new HoarderForceStartCommand(this));
        registerCommand(new HoarderInfoCommand(this));


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerCommand(SimpleCommand command) {
        commandManager.register(command);
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
