package dev.imlukas.hoarderplugin.event.data.item;

import org.bukkit.Material;

public class HoarderItem {

    private final Material material;
    private final double value;

    public HoarderItem(Material material, double value) {
        this.material = material;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public Material getMaterial() {
        return material;
    }
}
