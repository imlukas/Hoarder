package dev.imlukas.hoarderplugin.utils.menu.element;

import dev.imlukas.hoarderplugin.utils.menu.base.BaseMenu;

public abstract class Renderable {

    protected BaseMenu menu;
    private boolean active = true;

    public Renderable(BaseMenu menu) {
        this.menu = menu;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        forceUpdate();
    }

    public abstract void forceUpdate();

}
