package ir.ariwuh.plugin.ravensmp.api.language;

import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public final class RavenLanguageValue {

    public static final @NotNull String NOT_FOUND_VALUE_RESULT = "NOT_FOUND";
    public static final @NotNull RavenLanguageValue NOT_FOUND_VALUE = new RavenLanguageValue(NOT_FOUND_VALUE_RESULT);

    private final @Nullable Object value;

    public @NotNull String asText() {
        return castOrDefault(String.class, NOT_FOUND_VALUE_RESULT);
    }

    public @NotNull List<String> asTextList() {
        return extractStringList(Function.identity());
    }

    public @NotNull Component asComponent() {
        return deserialize(asText());
    }

    public @NotNull List<Component> asComponentList() {
        return extractStringList(MiniMessage.miniMessage()::deserialize);
    }

    public @NotNull String asParsedText(@NotNull Collection<RavenPlaceholder> placeholders) {
        return parsePlaceholder(asText(), placeholders);
    }

    public @NotNull List<String> asParsedTextList(@NotNull Collection<RavenPlaceholder> placeholders) {
        return extractStringList(text -> parsePlaceholder(text, placeholders));
    }

    public @NotNull Component asParsedComponent(@NotNull Collection<RavenPlaceholder> placeholders) {
        return deserialize(asParsedText(placeholders));
    }

    public @NotNull List<Component> asParsedPlaceholderComponentList(@NotNull Collection<RavenPlaceholder> placeholders) {
        return extractStringList(text -> deserialize(parsePlaceholder(text, placeholders)));
    }

    public int asNumber() {
        return castOrDefault(Integer.class, 1);
    }

    public boolean asBoolean() {
        return castOrDefault(Boolean.class, false);
    }

    private @NotNull <T> T castOrDefault(@NotNull Class<T> type, @NotNull T defaultValue) {
        return type.isInstance(this.value) ? type.cast(this.value) : defaultValue;
    }

    private @NotNull <T> List<T> extractStringList(@NotNull Function<String, T> mapper) {
        if (!(this.value instanceof List<?> rawList)) return new ArrayList<>();
        return rawList.stream()
                .filter(item -> item instanceof String)
                .map(item -> (String) item)
                .map(mapper)
                .collect(Collectors.toList());
    }

    private @NotNull String parsePlaceholder(@NotNull String text, @NotNull Collection<RavenPlaceholder> placeholders) {
        if (NOT_FOUND_VALUE_RESULT.equals(text) || placeholders.isEmpty()) return text;
        for (val placeholder : placeholders) {
            val replacement = placeholder.value() instanceof String
                    ? (String) placeholder.value()
                    : String.valueOf(placeholder.value());
            text = text.replace("<" + placeholder.id() + ">", replacement);
        }
        return text;
    }

    private @NotNull Component deserialize(@NotNull String text) {
        return NOT_FOUND_VALUE_RESULT.equals(text)
                ? Component.empty()
                : MiniMessage.miniMessage().deserialize(text);
    }

}