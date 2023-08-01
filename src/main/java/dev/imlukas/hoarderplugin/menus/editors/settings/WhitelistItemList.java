package dev.imlukas.hoarderplugin.menus.editors.settings;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.data.hoarder.item.HoarderItem;
import dev.imlukas.hoarderplugin.event.settings.EventSettings;
import dev.imlukas.hoarderplugin.event.settings.handler.EventSettingsHandler;
import dev.imlukas.hoarderplugin.event.settings.impl.hoarder.HoarderEventSettings;
import dev.imlukas.hoarderplugin.menus.ItemSelectionMenu;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.menu.template.FallbackMenu;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class WhitelistItemList extends UpdatableMenu {

    private final Messages messages;
    private final EventSettingsHandler eventSettingsHandler;
    private final HoarderEventSettings eventSettings;
    private final FallbackMenu fallbackMenu;

    private ConfigurableMenu menu;
    private PaginableArea area;

    public WhitelistItemList(HoarderPlugin plugin, Player viewer, FallbackMenu fallbackMenu, EventSettings eventSettings) {
        super(plugin, viewer);
        this.messages = plugin.getMessages();
        this.eventSettingsHandler = plugin.getEventSettingsHandler();

        this.fallbackMenu = fallbackMenu;
        this.eventSettings = (HoarderEventSettings) eventSettings;
        setup();
    }

    @Override
    public void refresh() {
        area.clear();
        for (HoarderItem whitelistedItem : eventSettings.getWhitelistedItems()) {
            Material item = whitelistedItem.getMaterial();
            double value = whitelistedItem.getValue();

            List<Placeholder<Player>> placeholderList = List.of(new Placeholder<>("value", String.valueOf(value)));

            Button itemButton = new Button(getApplicator().getItem("item").clone());
            itemButton.setItemPlaceholders(placeholderList);
            itemButton.getDisplayItem().setType(item);

            itemButton.setLeftClickAction(() -> {
                new ItemSelectionMenu(getPlugin(), getViewer(), item, (newItem) -> {
                    whitelistedItem.setMaterial(newItem);
                    itemButton.getDisplayItem().setType(newItem);
                    ScheduleBuilder.runIn1Tick(getPlugin(), this::open).sync().start();
                    messages.sendMessage(getViewer(), "editors.item.material",
                            new Placeholder<>("material", TextUtils.capitalize(newItem.name().toLowerCase())));
                    refresh();
                });
            });

            itemButton.setRightClickAction(() -> {
                messages.sendMessage(getViewer(), "inputs.value");
                holdForInput((newValue) -> {
                    whitelistedItem.setValue(Double.parseDouble(newValue));
                    messages.sendMessage(getViewer(), "editors.item.value",
                            new Placeholder<>("value", newValue));
                    refresh();
                });
            });

            itemButton.setMiddleClickAction(() -> {
                eventSettings.getWhitelistedItems().remove(whitelistedItem);
                messages.sendMessage(getViewer(), "editors.item.removed",
                        new Placeholder<>("material", TextUtils.capitalize(item.name().toLowerCase())));
                refresh();
            });

            area.addElement(itemButton);
        }

        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        ConfigurationApplicator applicator = getApplicator();
        area = new PaginableArea(applicator.getMask().selection("."));
        PaginableLayer paginableLayer = new PaginableLayer(menu, area);
        BaseLayer layer = new BaseLayer(menu);

        applicator.registerButton(layer, "p", paginableLayer::previousPage);
        applicator.registerButton(layer, "n", paginableLayer::nextPage);
        applicator.registerButton(layer, "c", () -> {
            eventSettingsHandler.updateWhitelist(eventSettings.getWhitelistedItems());
            fallbackMenu.openFallback();
        });

        applicator.registerButton(layer, "cr", () -> {
            new ItemSelectionMenu(getPlugin(), getViewer(), Material.STONE, (newItem) -> {
                messages.sendMessage(getViewer(), "inputs.value");
                holdForInput((value) -> {
                    HoarderItem item = new HoarderItem(newItem, Double.parseDouble(value));
                    eventSettings.getWhitelistedItems().add(item);
                    messages.sendMessage(getViewer(), "editors.item.added",
                            new Placeholder<>("material", TextUtils.capitalize(newItem.name().toLowerCase())),
                            new Placeholder<>("value", value));
                    refresh();
                });
            });
        });


        menu.addRenderable(layer, paginableLayer);
        refresh();
    }

    @Override
    public String getIdentifier() {
        return "whitelist-items";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }
}
