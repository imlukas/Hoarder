package dev.imlukas.hoarderplugin.event.storage;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class HoarderEventSettings {

    private final boolean randomMaterial;
    private final List<Material> blacklistedMaterials = new ArrayList<>();

    public HoarderEventSettings(boolean randomMaterial, List<Material> blacklistedMaterials) {
        this.randomMaterial = randomMaterial;
        this.blacklistedMaterials.addAll(blacklistedMaterials);
    }

    public boolean isRandomMaterial() {
        return randomMaterial;
    }

    public List<Material> getBlacklistedMaterials() {
        return blacklistedMaterials;
    }
}
