package dev.imlukas.hoarderplugin.listener;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.player.HoarderPlayerEventData;
import dev.imlukas.hoarderplugin.event.impl.Event;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
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
        if (tracker.getActiveEvent() == null) {
            return;
        }

        Event activeEvent = tracker.getActiveEvent();

        if (activeEvent instanceof HoarderEvent hoarder) {
            hoarder.getEventData().addParticipant(new HoarderPlayerEventData(player.getUniqueId()));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (tracker.getActiveEvent() == null) {
            return;
        }

        Event activeEvent = tracker.getActiveEvent();

        if (activeEvent instanceof HoarderEvent hoarder) {
            hoarder.getEventData().removeParticipant(player.getUniqueId());
        }
    }
}
