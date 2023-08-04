package dev.imlukas.hoarderplugin.utils.menu.element;

import dev.imlukas.hoarderplugin.utils.menu.base.BaseMenu;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class Renderable {

    protected BaseMenu menu;
    @Getter
    private boolean active = true;

    public Renderable(BaseMenu menu) {
        this.menu = menu;
    }

    public void setActive(boolean active) {
        this.active = active;
        forceUpdate();
    }

    public abstract void setItemPlaceholders(Placeholder<Player>... placeholders);

    public abstract void setItemPlaceholders(Collection<Placeholder<Player>> placeholders);

    public abstract void forceUpdate();

}
