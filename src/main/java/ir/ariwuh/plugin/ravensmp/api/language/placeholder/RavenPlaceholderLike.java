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
public final class RavenPlaceholderLike {

    private final @NotNull List<RavenPlaceholder> placeholders = new ArrayList<>();

    @Contract(pure = true)
    public static @NotNull RavenPlaceholderLike builder() {
        return new RavenPlaceholderLike();
    }

    @Contract("_, _ -> this")
    public @NotNull RavenPlaceholderLike append(@NotNull String id, @NotNull Object value) {
        this.placeholders.add(RavenPlaceholder.of(id, value));
        return this;
    }

    @Contract("_ -> this")
    public @NotNull RavenPlaceholderLike append(@NotNull RavenPlaceholderLike placeholderLike) {
        this.placeholders.addAll(placeholderLike.build());
        return this;
    }

    @Contract("_ -> this")
    public @NotNull RavenPlaceholderLike append(@NotNull RavenPlaceholder placeholder) {
        this.placeholders.add(placeholder);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull RavenPlaceholderLike append(@NotNull Collection<RavenPlaceholder> placeholders) {
        this.placeholders.addAll(placeholders);
        return this;
    }

    @Contract("_ -> this")
    public @NotNull RavenPlaceholderLike append(@NotNull RavenPlaceholder... placeholders) {
        this.placeholders.addAll(Arrays.stream(placeholders).toList());
        return this;
    }

    @Contract(pure = true)
    public @NotNull Collection<RavenPlaceholder> build() {
        return this.placeholders;
    }

}