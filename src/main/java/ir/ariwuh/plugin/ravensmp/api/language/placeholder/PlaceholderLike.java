package ir.ariwuh.plugin.ravensmp.api.language.placeholder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlaceholderLike {

    private final @NotNull List<Placeholder> placeholders = new ArrayList<>();

    @Contract(pure = true)
    public static @NotNull PlaceholderLike builder() {
        return new PlaceholderLike();
    }

    @Contract("_, _ -> this")
    public @NotNull PlaceholderLike append(@NotNull String id, @NotNull Object value) {
        this.placeholders.add(Placeholder.of(id, value));
        return this;
    }

    @Contract("_ -> this")
    public @NotNull PlaceholderLike append(@NotNull PlaceholderLike placeholderLike) {
        this.placeholders.addAll(placeholderLike.build());
        return this;
    }

    @Contract("_ -> this")
    public @NotNull PlaceholderLike append(@NotNull Placeholder placeholder) {
        this.placeholders.add(placeholder);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull PlaceholderLike append(@NotNull Collection<Placeholder> placeholders) {
        this.placeholders.addAll(placeholders);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull PlaceholderLike append(@NotNull Placeholder... placeholders) {
        this.placeholders.addAll(Arrays.stream(placeholders).toList());
        return this;
    }

    @Contract(pure = true)
    public @NotNull Collection<Placeholder> build() {
        return this.placeholders;
    }

}