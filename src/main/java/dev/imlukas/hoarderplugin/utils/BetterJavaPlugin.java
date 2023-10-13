package dev.imlukas.hoarderplugin.utils;

import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class BetterJavaPlugin extends JavaPlugin {

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void registerCommand(String name, CommandExecutor executor) {
        getCommand(name).setExecutor(executor);
    }
}
