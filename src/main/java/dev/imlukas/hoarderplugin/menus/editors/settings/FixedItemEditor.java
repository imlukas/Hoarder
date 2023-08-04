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
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.menu.selection.Selection;
import dev.imlukas.hoarderplugin.utils.schedulerutil.builders.ScheduleBuilder;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class FixedItemEditor extends UpdatableMenu {

    private final Messages messages;
    private final EventSettingsHandler eventSettingsHandler;
    private final HoarderEventSettings eventSettings;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private BaseLayer layer;

    public FixedItemEditor(HoarderPlugin plugin, Player viewer, EventSettings eventSettings) {
        super(plugin, viewer);
        this.messages = plugin.getMessages();
        this.eventSettingsHandler = plugin.getEventSettingsHandler();

        this.eventSettings = (HoarderEventSettings) eventSettings;
        setup();
    }

    @Override
    public void refresh() {
        HoarderItem fixedItem = eventSettings.getFixedItem();
        Material item = fixedItem.getMaterial();
        double value = fixedItem.getValue();

        List<Placeholder<Player>> placeholderList = List.of(new Placeholder<>("value", String.valueOf(value)));

        Button itemButton = new Button(getApplicator().getItem("item").clone());
        itemButton.setItemPlaceholders(placeholderList);
        itemButton.getDisplayItem().setType(item);

        itemButton.setLeftClickAction(() -> {
            new ItemSelectionMenu(getPlugin(), getViewer(), item, (newItem) -> {
                fixedItem.setMaterial(newItem.getType());
                itemButton.getDisplayItem().setType(newItem.getType());
                ScheduleBuilder.runIn1Tick(getPlugin(), this::open).sync().start();
                messages.sendMessage(getViewer(), "editors.item.material",
                        new Placeholder<>("material", TextUtils.capitalize(newItem.getType().name()).toLowerCase()));
                refresh();
            }).onClose(this::open);
        });

        itemButton.setClickWithItemTask((newItem) -> {
            fixedItem.setMaterial(newItem.getType());
            itemButton.getDisplayItem().setType(newItem.getType());
            ScheduleBuilder.runIn1Tick(getPlugin(), this::open).sync().start();
            messages.sendMessage(getViewer(), "editors.item.material",
                    new Placeholder<>("material", TextUtils.capitalize(newItem.getType().name()).toLowerCase()));
            refresh();
        });

        itemButton.setRightClickAction(() -> {
            messages.sendMessage(getViewer(), "inputs.value");
            holdForInput((newValue) -> {
                fixedItem.setValue(Double.parseDouble(newValue));
                messages.sendMessage(getViewer(), "editors.item.value",
                        new Placeholder<>("value", newValue));
                refresh();
            });
        });

        itemButton.setMiddleClickAction(() -> {
            HoarderItem newFixedItem = new HoarderItem(Material.STONE, 0);
            eventSettings.setFixedItem(newFixedItem);
            eventSettingsHandler.updatedFixed(newFixedItem);
            messages.sendMessage(getViewer(), "editors.item.fixed-removed");
            refresh();
        });

        Selection selection = applicator.getMask().selection("i");
        layer.applySelection(selection, itemButton);
        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = menu.getApplicator();
        layer = new BaseLayer(menu);

        applicator.registerButton(layer, "c", () -> {
            eventSettingsHandler.updatedFixed(eventSettings.getFixedItem());
            this.close();
        });

        menu.addRenderable(layer);
        refresh();
    }

    @Override
    public String getIdentifier() {
        return "fixed-item";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }
}
