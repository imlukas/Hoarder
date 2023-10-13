package dev.imlukas.hoarderplugin.menus.editors.settings;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.event.settings.handler.EventSettingsHandler;
import dev.imlukas.hoarderplugin.event.settings.impl.hoarder.HoarderEventSettings;
import dev.imlukas.hoarderplugin.event.settings.registry.EventSettingsRegistry;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.time.Time;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingsEditor extends UpdatableMenu {

    private final Messages messages;
    private final EventSettingsRegistry eventSettingsRegistry;
    private final EventSettingsHandler eventSettingsHandler;
    private final String eventIdentifier;


    private ConfigurableMenu menu;
    private BaseLayer layer;
    private ConfigurationApplicator applicator;

    public SettingsEditor(HoarderPlugin plugin, Player viewer, String identifier) {
        super(plugin, viewer);
        this.messages = plugin.getMessages();
        this.eventSettingsRegistry = plugin.getEventSettingsRegistry();
        this.eventSettingsHandler = plugin.getEventSettingsHandler();
        this.eventIdentifier = identifier;

        setup();
    }

    @Override
    public void setup() {
        menu = createMenu();
        layer = new BaseLayer(menu);
        applicator = menu.getApplicator();

        applicator.registerButton(layer, "c", this::close);

        menu.addRenderable(layer);
        refresh();
    }

    @Override
    public void refresh() {
        HoarderEventSettings eventSettings = eventSettingsRegistry.get(eventIdentifier);

        List<Placeholder<Player>> placeholderList = List.of(
                new Placeholder<>("starting-time", eventSettings.getStartingTime().toString()),
                new Placeholder<>("event-time", eventSettings.getEventTime().toString()),
                new Placeholder<>("random-material", eventSettings.isRandomMaterial() ? "true" : "false"));

        applicator.registerButton(layer, "s", () -> {
            messages.sendMessage(getViewer(), "inputs.time");
            holdForInput((newTime) -> {
                Time time = Time.parseTime(newTime);

                if (time == null) {
                    messages.sendMessage(getViewer(), "editors.invalid-time");
                    return;
                }

                eventSettings.setStartingTime(time);
                eventSettingsHandler.updateSetting("starting-time", newTime);
                sendUpdateMessage("starting-time", newTime);
                refresh();
            });
        });

        applicator.registerButton(layer, "e", () -> {
            messages.sendMessage(getViewer(), "inputs.time");
            holdForInput((newTime) -> {
                Time time = Time.parseTime(newTime);

                if (time == null) {
                    messages.sendMessage(getViewer(), "editors.invalid-time");
                    return;
                }

                eventSettings.setEventTime(time);
                eventSettingsHandler.updateSetting("event-time", newTime);
                sendUpdateMessage("event-time", newTime);
                refresh();
            });
        });

        applicator.registerButton(layer, "r", () -> {
            eventSettings.setRandomMaterial(!eventSettings.isRandomMaterial());
            eventSettingsHandler.updateSetting("random-material", eventSettings.isRandomMaterial());
            sendUpdateMessage("random-material", eventSettings.isRandomMaterial() ? "true" : "false");
            refresh();
        });

        applicator.registerButton(layer, "w", () -> new WhitelistItemList(getPlugin(), getViewer(), eventSettings).onClose(this::open).open());
        applicator.registerButton(layer, "f", () -> new FixedItemEditor(getPlugin(), getViewer(), eventSettings).onClose(this::open).open());

        menu.setItemPlaceholders(placeholderList);
        layer.setItemPlaceholders(placeholderList);
        menu.forceUpdate();
    }

    @Override
    public String getIdentifier() {
        return "settings-editor";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }

    public void sendUpdateMessage(String key, String value) {
        messages.sendMessage(getViewer(), "editors.setting.updated",
                new Placeholder<>("setting", key),
                new Placeholder<>("value", value));
    }
}
