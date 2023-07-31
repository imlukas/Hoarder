package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.Reference;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.mask.PatternMask;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ItemSelectionMenu extends UpdatableMenu {

    private final Messages messages;
    private final Material originalItem;
    private Material selectedItem;

    private final Consumer<Material> afterSetup;
    private final Reference<Boolean> found = new Reference<>(false);

    private ConfigurableMenu menu;

    public ItemSelectionMenu(HoarderPlugin plugin, Player player, Material selectedItem, Consumer<Material> afterSetup) {
        super(plugin, player);
        this.messages = plugin.getMessages();
        this.selectedItem = selectedItem;
        this.originalItem = selectedItem;

        this.afterSetup = afterSetup;

        setup();
        getMenuRegistry().getUpdatableMenuRegistry().register(this);
    }

    public void setup() {
        menu = createMenu();
        ConfigurationApplicator applicator = getApplicator();
        PatternMask mask = applicator.getMask();
        BaseLayer layer = new BaseLayer(menu);

        Player player = getViewer();

        CompletableFuture<Material> future = new CompletableFuture<>();
        future.thenRun(player::closeInventory);

        PaginableLayer paginableLayer = new PaginableLayer(menu);
        menu.addRenderable(layer, paginableLayer);

        PaginableArea area = new PaginableArea(mask.selection("."));

        Button selectedItemButton = applicator.registerButton(layer, "i", (event) -> {
            event.setCancelled(true);
            afterSetup.accept(selectedItem);

            found.set(true);
            player.closeInventory();
        });

        applicator.registerButton(layer, "s", (event) -> {
            event.setCancelled(true);
            if (selectedItem == null) {
                afterSetup.accept(originalItem);
                player.closeInventory();
                return;
            }

            afterSetup.accept(selectedItem);
            player.closeInventory();
        });

        applicator.registerButton(layer, "n", paginableLayer::nextPage);
        applicator.registerButton(layer, "p", paginableLayer::previousPage);

        applicator.registerButton(layer, "b", (event) -> {
            future.complete(originalItem);
        });

        applicator.registerButton(layer, "t", () -> {
           holdForInput((text) -> {
                text = text.replace(" ", "_").toUpperCase();
                Material material = Material.matchMaterial(text);

                if (material == null || material.isAir() || !material.isItem()) {
                    messages.sendMessage(player, "item-selection.invalid-item");
                    return;
                }

                this.selectedItem = material;
                updateButton(selectedItemButton, menu);
            });
        });

        Button itemButton = new Button(applicator.getItem("item"));

        for (Material material : Material.values()) {
            if (material.isAir() || !material.isItem()) {
                continue;
            }

            Button button = (Button) itemButton.copy();
            button.getDisplayItem().setType(material);

            button.setLeftClickAction(() -> {
                this.selectedItem = material;
                updateButton(selectedItemButton, menu);
            });

            area.addElement(button);
        }

        selectedItemButton.getDisplayItem().setType(selectedItem);
        paginableLayer.addArea(area);
        player.closeInventory();

        menu.onClose(() -> {
            if (found.get()) {
                future.complete(selectedItem);
            }
        });
        menu.forceUpdate();
        menu.open();
    }

    private void updateButton(Button button, ConfigurableMenu menu) {
        button.getDisplayItem().setType(selectedItem);
        menu.forceUpdate();
    }

    @Override
    public void refresh() {
        menu.forceUpdate();
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }

    @Override
    public String getIdentifier() {
        return "item-selection";
    }
}
