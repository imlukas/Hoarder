package dev.imlukas.hoarderplugin.utils.command.language.type.impl;

import dev.imlukas.hoarderplugin.utils.command.language.type.ParameterType;
import dev.imlukas.hoarderplugin.utils.text.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface FilteredParameterType<Type> extends ParameterType<Type> {

    List<Type> getAllValues();

    @Nullable
    List<Placeholder<Player>> createPlaceholders(Object value);

}
