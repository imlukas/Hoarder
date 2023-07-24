package dev.imlukas.hoarderplugin.event.data.item;

import org.bukkit.Material;

public class HoarderItem {

    private final Material material;
    private final Double value;

    public HoarderItem(Material material, Double value) {
        this.material = material;
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public Material getMaterial() {
        return material;
    }
}
