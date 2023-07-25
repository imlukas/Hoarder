package dev.imlukas.hoarderplugin.utils.menu.layer;

import dev.imlukas.hoarderplugin.utils.menu.base.BaseMenu;
import dev.imlukas.hoarderplugin.utils.menu.element.Renderable;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PaginableLayer extends Renderable {

    private final List<PaginableArea> areas = new ArrayList<>();
    private int page = 1;

    public PaginableLayer(BaseMenu menu) {
        super(menu);
    }

    @Override
    public void setItemPlaceholders(Collection<Placeholder<Player>> placeholders) {
        for (PaginableArea area : areas) {
            area.getElements().forEach(element -> element.setItemPlaceholders(placeholders));
        }
    }

    @Override
    public void forceUpdate() {
        for (PaginableArea area : areas) {
            area.forceUpdate(menu, page);
        }
    }

    public void addArea(PaginableArea... area) {
        areas.addAll(List.of(area));
    }

    public int getPage() {
        return page;
    }

    public void nextPage() {
        if (page >= getMaxPage()) {
            return;
        }

        page++;
        menu.forceUpdate();
    }

    public void previousPage() {
        if (page == 1) {
            return;
        }
        page--;
        menu.forceUpdate();
    }

    public int getMaxPage() {
        int max = 0;
        for (PaginableArea area : areas) {
            int areaMax = area.getPageCount();
            if (areaMax > max) {
                max = areaMax;
            }
        }
        return max;
    }

}
