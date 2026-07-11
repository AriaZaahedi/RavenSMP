package ir.ariwuh.plugin.ravensmp.utility;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public final class StringUtility {

    public @NotNull String formatTagColorName(@NotNull String input) {
        return Arrays.stream(input.split("_"))
                .filter(s -> !s.isEmpty())
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "));
    }

    public @Nullable NamedTextColor stringToNamedTextColor(@NotNull String color) {
        return NamedTextColor.NAMES.value(color.toLowerCase());
    }

}