package dev.imlukas.hoarderplugin.event.data.hoarder.item;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public class HoarderItem {

    private Material material;
    private double value;

    public HoarderItem(Material material, double value) {
        this.material = material;
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
