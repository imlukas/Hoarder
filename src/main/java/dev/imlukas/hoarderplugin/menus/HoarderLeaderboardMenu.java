package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.constants.LeaderboardType;
import dev.imlukas.hoarderplugin.storage.cache.PlayerStats;
import dev.imlukas.hoarderplugin.storage.sql.SQLHandler;
import dev.imlukas.hoarderplugin.utils.collection.MapUtils;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.button.MultiSwitch;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class HoarderLeaderboardMenu extends UpdatableMenu {

    private final Map<UUID, PlayerStats> playerStatsMap = new HashMap<>();
    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private LeaderboardType type = LeaderboardType.WINS;
    private PaginableArea area;
    private MultiSwitch<LeaderboardType> switcher;

    public HoarderLeaderboardMenu(HoarderPlugin plugin, Player viewer) {
        super(plugin, viewer);
        SQLHandler sqlHandler = plugin.getSQLHandler();
        sqlHandler.fetchEventStats().thenAccept(playerStatsMap::putAll).thenRun(() -> {
            this.setup();
            open();
        });
    }

    @Override
    public void refresh() {
        area.clear();
        Map<PlayerStats, Integer> leaderboard = new HashMap<>();
        Placeholder<Player> typePlaceholder = new Placeholder<>("type", TextUtils.enumToText(type));
        switcher.setItemPlaceholders(typePlaceholder);
        for (PlayerStats playerStats : playerStatsMap.values()) {
            switch (type) {
                case WINS -> leaderboard.put(playerStats, playerStats.getWins());
                case SOLD_ITEMS -> leaderboard.put(playerStats, playerStats.getSoldItems());
                case TOP3 -> leaderboard.put(playerStats, playerStats.getTop3());
            }
        }

        Map<Integer, PlayerStats> sortedLeaderboard = MapUtils.getLeaderboardMap(leaderboard);

        for (Map.Entry<Integer, PlayerStats> leaderboardEntry : sortedLeaderboard.entrySet()) {

            List<Placeholder<Player>> placeholders = new ArrayList<>();
            placeholders.add(new Placeholder<>("position", String.valueOf(leaderboardEntry.getKey())));
            placeholders.add(new Placeholder<>("player", leaderboardEntry.getValue().getOfflinePlayer().getName()));
            placeholders.add(new Placeholder<>("wins", String.valueOf(leaderboardEntry.getValue().getWins())));
            placeholders.add(new Placeholder<>("sold_items", String.valueOf(leaderboardEntry.getValue().getSoldItems())));
            placeholders.add(new Placeholder<>("top3", String.valueOf(leaderboardEntry.getValue().getTop3())));

            Button leaderboardEntryButton = new Button(applicator.getItem("entry"));
            leaderboardEntryButton.setItemPlaceholders(placeholders);
            area.addElement(leaderboardEntryButton);
        }
        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = getApplicator();
        area = new PaginableArea(applicator.getMask().selection("."));
        PaginableLayer paginableLayer = new PaginableLayer(menu);
        paginableLayer.addArea(area);
        BaseLayer layer = new BaseLayer(menu);

        applicator.registerButton(layer, "c", this::close);

        Button typeButton = new Button(applicator.getItem("t"));
        switcher = new MultiSwitch<>(typeButton, LeaderboardType.values());
        switcher.setChoice(type);
        switcher.onChoiceUpdate(type -> {
            this.type = type;
            refresh();
        });

        layer.applyRawSelection(applicator.getMask().selection("t"), switcher);

        menu.addRenderable(layer, paginableLayer);
        refresh();
    }

    @Override
    public String getIdentifier() {
        return "hoarder-leaderboard";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }
}
