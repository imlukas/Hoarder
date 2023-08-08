package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.PlayerEventData;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.button.DecorationItem;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.mask.PatternMask;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HoarderRewardsMenu extends UpdatableMenu {

    private final HoarderEvent lastEvent;
    private final Messages messages;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private PaginableArea area;

    public HoarderRewardsMenu(HoarderPlugin plugin, Player viewer, @NotNull HoarderEvent lastEvent) {
        super(plugin, viewer);
        this.messages = plugin.getMessages();
        this.lastEvent = lastEvent;
        setup();
    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = getApplicator();
        area = new PaginableArea(applicator.getMask().selection("t"), new DecorationItem(applicator.getItem("unavailable")));
        PaginableLayer paginableLayer = new PaginableLayer(menu);
        paginableLayer.addArea(area);

        BaseLayer layer = new BaseLayer(menu);

        applicator.registerButton(layer, "c", this::close);
        menu.addRenderable(layer, paginableLayer);
        refresh();
    }

    @Override
    public void refresh() {
        area.clear();
        PlayerEventData playerData = lastEvent.getEventData().getPlayerData(getViewerId());

        if (playerData == null) {
            messages.sendMessage(getViewer(), "hoarder.no-participation");
            return;
        }

        LinkedList<EventPrize> prizes = playerData.getAvailablePrizes();

        menu.onClose(() -> prizes.removeIf(EventPrize::isClaimed));

        int notClaimedPrizes = 0;
        for (EventPrize prize : prizes) {
            if (!prize.isClaimed()) {
                notClaimedPrizes++;
            }
        }

        for (EventPrize prize : prizes) {
            List<Placeholder<Player>> placeholderList = List.of(new Placeholder<>("remaining-prizes", String.valueOf(notClaimedPrizes - 1)),
                    new Placeholder<>("reward-number", String.valueOf(prizes.indexOf(prize) + 1)),
                    new Placeholder<>("prize-name", prize.getDisplayName()));

            Button button = new Button(applicator.getItem("unclaimed").clone());
            button.setItemPlaceholders(placeholderList);
            button.setLeftClickAction(() -> {
                if (prize.isClaimed()) {
                    messages.sendMessage(getViewer(), "prize.claimed");
                    return;
                }

                prize.runAll(getViewer());
                prize.setClaimed(true);
                messages.sendMessage(getViewer(), "prize.claim", placeholderList);
                refresh();
            });

            button.getDisplayItem().setType(prize.getDisplayItem().getType());

            if (prize.isClaimed()) {
                button.setDisplayItem(applicator.getItem("claimed"));
            }

            area.addElement(button);
        }

        menu.forceUpdate();
    }

    @Override
    public String getIdentifier() {
        return "hoarder-rewards";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }
}
