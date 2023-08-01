package dev.imlukas.hoarderplugin.menus.editors.prize;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.actions.registry.ActionRegistry;
import dev.imlukas.hoarderplugin.utils.menu.base.ConfigurableMenu;
import dev.imlukas.hoarderplugin.utils.menu.button.Button;
import dev.imlukas.hoarderplugin.utils.menu.configuration.ConfigurationApplicator;
import dev.imlukas.hoarderplugin.utils.menu.layer.BaseLayer;
import dev.imlukas.hoarderplugin.utils.menu.layer.PaginableLayer;
import dev.imlukas.hoarderplugin.utils.menu.pagination.PaginableArea;
import dev.imlukas.hoarderplugin.utils.menu.registry.communication.UpdatableMenu;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.function.Consumer;

public class ActionCreatorMenu extends UpdatableMenu {

    private final ActionRegistry actionRegistry;
    private final Messages messages;

    private final Consumer<LinkedList<PrizeAction>> afterSetup;
    private final LinkedList<PrizeAction> actions;

    private ConfigurableMenu menu;
    private ConfigurationApplicator applicator;
    private PaginableArea area;


    public ActionCreatorMenu(HoarderPlugin plugin, Player viewer, Consumer<LinkedList<PrizeAction>> afterSetup) {
        this(plugin, viewer, new LinkedList<>(), afterSetup);
    }

    public ActionCreatorMenu(HoarderPlugin plugin, Player viewer, LinkedList<PrizeAction> actions, Consumer<LinkedList<PrizeAction>> afterSetup) {
        super(plugin, viewer);
        this.actions = actions;
        this.actionRegistry = plugin.getActionRegistry();
        this.messages = plugin.getMessages();

        this.afterSetup = afterSetup;
        setup();
        open();
    }

    @Override
    public void refresh() {
        area.clear();


        for (int i = 0; i < actions.size(); i++) {
            PrizeAction action = actions.get(i);
            Placeholder<Player> actionInputPlaceholder = new Placeholder<>("action", action.getFullInput());
            Button button = applicator.makeButton("item");
            button.setItemPlaceholders(actionInputPlaceholder);
            int finalIndex = i;

            button.setMiddleClickAction(() -> {
                messages.sendMessage(getViewer(), "inputs.order", new Placeholder<>("max", String.valueOf(actions.size())));
                holdForInput((input) -> {
                    int newIndex = TextUtils.parseInt(input) - 1;

                    if (newIndex < 0 || newIndex >= actions.size()) {
                        messages.sendMessage(getViewer(), "editors.invalid-action");
                        return;
                    }

                    actions.remove(finalIndex);
                    actions.add(newIndex, action);
                    messages.sendMessage(getViewer(), "editors.action.order-change",
                            actionInputPlaceholder,
                            new Placeholder<>("new-order", input));
                    refresh();
                });
            });

            button.setLeftClickAction(() -> {
                messages.sendMessage(getViewer(), "inputs.edit");
                holdForInput((input) -> {
                    if (!input.contains(":")) {
                        action.setInput(input);
                        messages.sendMessage(getViewer(), "editors.action.changed", actionInputPlaceholder, new Placeholder<>("new-action", input));
                        return;
                    }

                    String identifier = input.substring(0, input.indexOf(":"));

                    if (!identifier.equals(action.getIdentifier())) {
                        PrizeAction newAction = actionRegistry.getAction(input);

                        if (newAction == null) {
                            messages.sendMessage(getViewer(), "editors.invalid-action");
                            return;
                        }

                        actions.set(finalIndex, newAction);
                    }

                    messages.sendMessage(getViewer(), "editors.action.changed", actionInputPlaceholder, new Placeholder<>("new-action", input));
                    refresh();
                });
            });

            button.setRightClickAction(() -> {
                messages.sendMessage(getViewer(), "editors.action.removed", actionInputPlaceholder);
                actions.remove(finalIndex);
                refresh();
            });

            area.addElement(button);
        }


        menu.forceUpdate();
    }

    @Override
    public void setup() {
        menu = createMenu();
        applicator = getApplicator();
        area = new PaginableArea(applicator.getMask().selection("."));

        BaseLayer baseLayer = new BaseLayer(menu);
        PaginableLayer paginableLayer = new PaginableLayer(menu);
        paginableLayer.addArea(area);

        applicator.registerButton(baseLayer, "p", paginableLayer::previousPage);
        applicator.registerButton(baseLayer, "n", paginableLayer::nextPage);

        applicator.registerButton(baseLayer, "c", () -> {
            afterSetup.accept(actions);
        });

        applicator.registerButton(baseLayer, "cr", () -> {
            messages.sendMessage(getViewer(), "inputs.action");
            holdForInput((action) -> {
                PrizeAction prizeAction = actionRegistry.getAction(action);

                if (prizeAction == null) {
                    messages.sendMessage(getViewer(), "editors.invalid-action");
                    return;
                }

                actions.add(prizeAction);
                messages.sendMessage(getViewer(), "editors.action.added", new Placeholder<>("action", action));
                refresh();
            });
        });

        menu.addRenderable(baseLayer, paginableLayer);
        refresh();
    }

    @Override
    public String getIdentifier() {
        return "action-editor";
    }

    @Override
    public ConfigurableMenu getMenu() {
        return menu;
    }
}
