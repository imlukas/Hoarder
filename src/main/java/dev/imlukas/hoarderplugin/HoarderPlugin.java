package dev.imlukas.hoarderplugin;

import dev.imlukas.hoarderplugin.command.HoarderCommand;
import dev.imlukas.hoarderplugin.command.HoarderForceStartCommand;
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
import dev.imlukas.hoarderplugin.utils.menu.registry.MenuRegistry;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class HoarderPlugin extends JavaPlugin {

    private CommandManager commandManager;
    private MenuRegistry menuRegistry;
    private Messages messages;

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
        commandManager = new CommandManager(this);
        menuRegistry = new MenuRegistry(this);
        messages = new Messages(this);

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

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerCommand(SimpleCommand command) {
        commandManager.register(command);
    }
}
