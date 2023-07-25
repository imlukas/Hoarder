package dev.imlukas.hoarderplugin.utils.text;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextUtils {

    private static final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]){6}");

    public static String color(String message) {
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, ChatColor.of(color) + "");
            matcher = hexPattern.matcher(message);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String enumToText(Enum<?> enumToText) {
        return capitalize(enumToText.toString().replace("_", " "));
    }

    public static String capitalize(String toCapitalize) {
        return toCapitalize.substring(0, 1).toUpperCase() + toCapitalize.substring(1);
    }

    public static TextComponent toComponent(String message) {
        return new TextComponent(color(message));
    }

    /**
     * Parses a String to an integer, throwing an IllegalArgumentException if the String is not a valid integer.
     *
     * @param stringToParse The String to parse
     * @param predicate     A Predicate to test the parsed integer against
     * @return The parsed integer
     */
    public static int parseInt(String stringToParse, Predicate<Integer> predicate) { // not really a text utility, but it's used in a text utility
        int parsed = 1;
        try {
            parsed = Integer.parseInt(stringToParse);
        } catch (NumberFormatException e) {
            System.err.println("Invalid number: " + stringToParse);
            return 1;
        }

        if (!predicate.test(parsed)) {
            System.err.println("Invalid number: " + stringToParse);
            return 1;
        }

        return parsed;
    }

}

