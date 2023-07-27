package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.player.PlayerEventData;
import dev.imlukas.hoarderplugin.event.impl.HoarderEvent;
import dev.imlukas.hoarderplugin.event.registry.EventRegistry;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.mask.PatternMask;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.menu.selection.Selection;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HoarderRewardsMenu extends UpdatableMenu {

    private final EventRegistry eventRegistry;
    private final Messages messages;
    private final HoarderEvent lastEvent;
    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private PatternMask mask;
    private BaseLayer layer;

    public HoarderRewardsMenu(HoarderPlugin plugin, Player viewer, @NotNull HoarderEvent lastEvent) {
        super(plugin, viewer);
        this.eventRegistry = plugin.getEventRegistry();
        this.messages = plugin.getMessages();
        this.lastEvent = lastEvent;
        setup();
    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = getApplicator();
        mask = applicator.getMask();
        layer = new BaseLayer(menu);

        applicator.registerButton(layer, "c", this::close);

        menu.addRenderable(layer);
        refresh();

    }


    @Override
    public void refresh() {
        PlayerEventData playerData = lastEvent.getEventData().getPlayerData(getViewerId());
        Map<EventPrize, Boolean> availablePrizes = playerData.getAvailablePrizes();
        List<EventPrize> prizes = new ArrayList<>(availablePrizes.keySet());
        int prizeAmount = availablePrizes.size();
        int availablePrizesAmount = (int) availablePrizes.values().stream().filter(claimed -> !claimed).count() - 1;

        if (availablePrizesAmount <= 0) {
            messages.sendMessage(getViewer(), "prize.no-claim");
            this.close();
            return;
        }

        for (int i = 1; i <= 3; i++) {

            if (i > prizeAmount) {
                layer.applyRawSelection(mask.selection(String.valueOf(i)), new Button(applicator.getItem("unavailable")));
                continue;
            }

            EventPrize prize = prizes.get(i - 1);

            List<Placeholder<Player>> placeholderList = List.of(new Placeholder<>("remaining-prizes", String.valueOf(availablePrizesAmount)),
                    new Placeholder<>("reward-number", String.valueOf(i)),
                    new Placeholder<>("prize-name", prize.getDisplayName()));

            Button button = new Button(applicator.getItem("unclaimed"));

            button.setItemPlaceholders(placeholderList);
            button.setLeftClickAction(() -> {
                if (availablePrizes.get(prize)) {
                    messages.sendMessage(getViewer(), "prize.claimed");
                    return;
                }

                prize.runAll(getViewer());
                playerData.setAvailablePrize(prize, true);
                messages.sendMessage(getViewer(), "prize.claim", placeholderList);
                refresh();
            });

            if (availablePrizes.get(prize)) {
                button.setDisplayItem(applicator.getItem("claimed"));
            }

            layer.applyRawSelection(mask.selection(String.valueOf(i)), button);
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
