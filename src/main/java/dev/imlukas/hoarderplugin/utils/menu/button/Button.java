package dev.imlukas.hoarderplugin.utils.menu.button;

import dev.imlukas.hoarderplugin.utils.menu.element.MenuElement;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Button implements MenuElement {

    private ItemStack displayItem;
    private Consumer<InventoryClickEvent> clickTask;
    private Consumer<InventoryClickEvent> rightClickTask;
    private Consumer<InventoryClickEvent> leftClickTask;
    private Consumer<InventoryClickEvent> middleClickTask;

    private Collection<Placeholder<Player>> placeholders;

    public Button(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public Button(ItemStack displayItem, Consumer<InventoryClickEvent> clickTask) {
        this.displayItem = displayItem;
        this.clickTask = clickTask;
    }

    public void setClickAction(Consumer<InventoryClickEvent> clickTask) {
        this.clickTask = clickTask;
    }

    public void setRightClickAction(Runnable clickTask) {
        this.rightClickTask = event -> clickTask.run();
    }

    public void setLeftClickAction(Runnable clickTask) {
        this.leftClickTask = event -> clickTask.run();
    }

    public void setMiddleClickAction(Runnable clickTask) {
        this.middleClickTask = event -> clickTask.run();
    }

    @Override
    public void handle(InventoryClickEvent event) {
        ClickType clickType = event.getClick();

        if (clickTask != null) {
            clickTask.accept(event);
            return;
        }

        if (clickType == ClickType.LEFT && leftClickTask != null) {
            leftClickTask.accept(event);
            return;
        }
        if (clickType == ClickType.RIGHT && rightClickTask != null) {
            rightClickTask.accept(event);
        }

        if (clickType == ClickType.MIDDLE && middleClickTask != null) {
            middleClickTask.accept(event);
        }

    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public void setPlaceholders(Collection<Placeholder<Player>> placeholders) {
        this.placeholders = placeholders;
    }

    public void setPlaceholders(Placeholder<Player>... placeholders) {
        this.placeholders = List.of(placeholders);
    }

    @Override
    public MenuElement copy() {
        return new Button(displayItem.clone(), clickTask, rightClickTask, leftClickTask, middleClickTask, placeholders);
    }

    @Override
    public Collection<Placeholder<Player>> getItemPlaceholders() {
        return placeholders;
    }

    @Override
    public MenuElement setItemPlaceholders(Collection<Placeholder<Player>> placeholders) {
        this.placeholders = placeholders;
        return this;
    }

    public MenuElement setItemPlaceholders(Placeholder<Player>... placeholders) {
        this.placeholders = Arrays.stream(placeholders).toList();
        return this;
    }


    public void setDisplayMaterial(Material material) {
        displayItem.setType(material);
    }

    public void setDisplayMaterial(String material) {
        displayItem.setType(Material.valueOf(material));
    }

    public void onClick(ClickType clickType, Consumer<InventoryClickEvent> clickTask) {
        this.clickTask = clickTask;
    }
}
