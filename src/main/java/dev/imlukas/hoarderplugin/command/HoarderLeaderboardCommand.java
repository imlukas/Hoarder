package dev.imlukas.hoarderplugin.command;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.menus.HoarderLeaderboardMenu;
import dev.imlukas.hoarderplugin.menus.HoarderMainMenu;
import dev.imlukas.hoarderplugin.utils.command.command.impl.AdvancedCommand;
import dev.imlukas.hoarderplugin.utils.command.command.impl.ExecutionContext;
import dev.imlukas.hoarderplugin.utils.command.legacy.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HoarderLeaderboardCommand implements SimpleCommand {

    private final HoarderPlugin plugin;

    public HoarderLeaderboardCommand(HoarderPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "hoarder.leaderboard";
    }

    @Override
    public void execute(CommandSender sender, String... args) {
        new HoarderLeaderboardMenu(plugin, (Player) sender);
    }
}
