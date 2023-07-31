package dev.imlukas.hoarderplugin.utils.storage;

import dev.imlukas.hoarderplugin.HoarderPlugin;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import dev.imlukas.hoarderplugin.utils.text.TextUtils;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Messages extends YMLBase {

    private final String prefix;
    protected boolean usePrefix, useActionbar;
    private String msg;

    public Messages(HoarderPlugin plugin) {
        super(plugin, new File(plugin.getDataFolder(), "messages.yml"), true);
        prefix = getConfiguration().getString("messages.prefix");
        usePrefix = getConfiguration().getBoolean("messages.use-prefix");
        useActionbar = getConfiguration().getBoolean("messages.use-actionbar");
    }

    private String setMessage(String name, Function<String, String> action) {
        if (!getConfiguration().contains("messages." + name)) {
            return "";
        }

        msg = getMessage(name).replaceAll("%prefix%", prefix);

        if (usePrefix) {
            msg = prefix + " " + getMessage(name);
        }

        msg = action.apply(msg);
        return TextUtils.color(msg);
    }

    public void sendMessage(CommandSender sender, String name) {
        sendMessage(sender, name, (s) -> s);
    }

    @SafeVarargs
    public final <T extends CommandSender> void sendMessage(T sender, String name, Placeholder<T>... placeholders) {
        sendMessage(sender, name, List.of(placeholders));
    }

    public final <T extends CommandSender> void sendMessage(T sender, String name, Collection<Placeholder<T>> placeholders) {
        sendMessage(sender, name, (text) -> {
            for (Placeholder<T> placeholder : placeholders) {
                text = placeholder.replace(text, sender);
            }

            return text;
        });
    }


    public void sendMessage(CommandSender sender, String name, Function<String, String> action) {
        if (getConfiguration().isList("messages." + name)) {
            for (String message : getConfiguration().getStringList("messages." + name)) {
                msg = message.replace("%prefix%", prefix);
                msg = TextUtils.color(action.apply(msg));
                sender.sendMessage(msg);
            }
            return;
        }

        msg = setMessage(name, action);
        if (useActionbar && sender instanceof Player player) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextUtils.toComponent(msg));
        } else {
            sender.sendMessage(msg);
        }
    }

    public String getMessage(String name) {
        return getConfiguration().getString("messages." + name);
    }

    public String getMessage(String name, Placeholder<CommandSender>... placeholders) {
        String message = getMessage(name);

        if (message == null) {
            return null;
        }

        for (Placeholder<CommandSender> placeholder : placeholders) {
            message = placeholder.replace(message, null);
        }

        return message;
    }
}

