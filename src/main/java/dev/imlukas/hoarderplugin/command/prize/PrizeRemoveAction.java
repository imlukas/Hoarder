package dev.imlukas.hoarderplugin.command.prize;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.prize.EventPrize;
import dev.imlukas.hoarderplugin.prize.actions.PrizeAction;
import dev.imlukas.hoarderplugin.prize.registry.PrizeRegistry;
import dev.imlukas.hoarderplugin.prize.storage.PrizeHandler;
import dev.imlukas.hoarderplugin.utils.command.command.impl.AdvancedCommand;
import dev.imlukas.hoarderplugin.utils.command.command.impl.ExecutionContext;
import dev.imlukas.hoarderplugin.utils.command.language.type.Parameter;
import dev.imlukas.hoarderplugin.utils.command.language.type.impl.StringParameterType;
import dev.imlukas.hoarderplugin.utils.component.ComponentEvent;
import dev.imlukas.hoarderplugin.utils.component.ComponentUtil;
import dev.imlukas.hoarderplugin.utils.storage.Messages;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

public class PrizeRemoveAction extends AdvancedCommand {
    private final Messages messages;
    private final PrizeRegistry prizeRegistry;
    private final PrizeHandler prizeHandler;

    public PrizeRemoveAction(HoarderPlugin plugin) {
        super("prize action remove <identifier> <index>");
        this.messages = plugin.getMessages();
        this.prizeRegistry = plugin.getPrizeRegistry();
        this.prizeHandler = plugin.getPrizeHandler();

        registerParameter(new Parameter<>("identifier", new StringParameterType(), false));
        registerParameter(new Parameter<>("index", new StringParameterType(), false));
    }

    @Override
    public String getPermission() {
        return "hoarder.prize.edit";
    }

    @Override
    public void execute(CommandSender sender, ExecutionContext context) {
        EventPrize prize = prizeRegistry.getPrize(context.getParameter("identifier"));
        int index = Integer.parseInt(context.getParameter("index"));

        if (prize == null) {
            sender.sendMessage("Prize not found");
            return;
        }

        if (index < 0 || index >= prize.getActions().size()) {
            sender.sendMessage("Action index out of bounds");
            return;
        }

        PrizeAction action = prize.getActions().remove(index);
        prizeHandler.updatePrize(prize);
        messages.sendMessage(sender, "editors.action.removed", new Placeholder<>("action", action.getFullInput()));

        for (int i = 0; i < 10; i++) {
            sender.sendMessage("");
        }

        TextComponent component = ComponentUtil.create(
                messages.getMessage("editors.action.entry", new Placeholder<>("action", action.getFullInput())),
                ComponentEvent.Hover.showText(messages.getMessage("editors.action.hover-delete")),
                ComponentEvent.Click.runCommand("/prize action remove " + prize.getIdentifier() + " " + index));

        sender.sendMessage(component);
    }
}
