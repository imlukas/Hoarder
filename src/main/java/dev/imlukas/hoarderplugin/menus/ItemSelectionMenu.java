package dev.imlukas.hoarderplugin.menus;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.Reference;
import dev.imlukas.hoarderplugin.utils.item.ItemUtil;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.mask.PatternMask;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ItemSelectionMenu extends UpdatableMenu {

    private final Messages messages;
    private final ItemStack originalItem;
    private final ItemStack selectedItem;

    private final Consumer<ItemStack> afterSetup;
    private final Reference<Boolean> found = new Reference<>(false);

    private ConfigurableMenu menu;

    public ItemSelectionMenu(HoarderPlugin plugin, Player player, Material selectedItem, Consumer<ItemStack> afterSetup) {
        this(plugin, player, new ItemStack(selectedItem), afterSetup);
    }

    public ItemSelectionMenu(HoarderPlugin plugin, Player player, ItemStack selectedItem, Consumer<ItemStack> afterSetup) {
        super(plugin, player);
        this.messages = plugin.getMessages();

        this.selectedItem = selectedItem.clone();
        this.originalItem = selectedItem.clone();

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

        CompletableFuture<ItemStack> future = new CompletableFuture<>();
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
            if (selectedItemButton == null) {
                afterSetup.accept(originalItem);
                player.closeInventory();
                return;
            }

            afterSetup.accept(selectedItem);
            player.closeInventory();
        });

        applicator.registerButton(layer, "n", paginableLayer::nextPage);
        applicator.registerButton(layer, "p", paginableLayer::previousPage);
        applicator.registerButton(layer, "m", () -> {
            holdForInput((input) -> {
                TextUtils.parseInt(input).ifPresent((modelData) -> {
                    ItemUtil.setModelData(selectedItem, modelData);
                    updateButton(selectedItemButton, menu, modelData);
                    refresh();
                });
            });
        });

        applicator.registerButton(layer, "b", (event) -> {
            future.complete(originalItem);
        });

        applicator.registerButton(layer, "t", () -> {
            messages.sendMessage(player, "inputs.material");
            holdForInput((text) -> {
                text = text.replace(" ", "_").toUpperCase();
                Material material = Material.matchMaterial(text);

                if (material == null || material.isAir() || !material.isItem()) {
                    messages.sendMessage(player, "item-selection.invalid-item");
                    return;
                }

                this.selectedItem.setType(material);
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
                this.selectedItem.setType(material);
                updateButton(selectedItemButton, menu);
            });

            area.addElement(button);
        }

        selectedItemButton.getDisplayItem().setType(selectedItem.getType());
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

    private void updateButton(Button button, ConfigurableMenu menu, int modelData) {
        button.getDisplayItem().setType(selectedItem.getType());
        ItemUtil.setModelData(button.getDisplayItem(), modelData);
        menu.forceUpdate();
    }

    private void updateButton(Button button, ConfigurableMenu menu) {
        button.getDisplayItem().setType(selectedItem.getType());
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
