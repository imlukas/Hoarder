package dev.imlukas.hoarderplugin.listener;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.Event;
import dev.imlukas.hoarderplugin.event.tracker.EventTracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisconnectListener implements Listener {

    private final EventTracker tracker;

    public DisconnectListener(HoarderPlugin plugin) {
        this.tracker = plugin.getEventTracker();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Event activeEvent = tracker.getActiveEvent();

        if (activeEvent == null) {
            return;
        }

        activeEvent.getEventData().addParticipant(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Event activeEvent = tracker.getActiveEvent();

        if (activeEvent == null) {
            return;
        }

        activeEvent.getEventData().removeParticipant(player);
    }
}
