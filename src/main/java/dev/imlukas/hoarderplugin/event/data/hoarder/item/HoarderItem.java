package dev.imlukas.hoarderplugin.event.data.hoarder.item;

import org.bukkit.Material;

public class HoarderItem {

    private Material material;
    private double value;

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

    public void setValue(double value) {
        this.value = value;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
