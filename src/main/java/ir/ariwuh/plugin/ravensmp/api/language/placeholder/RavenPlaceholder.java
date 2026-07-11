package ir.ariwuh.plugin.ravensmp.api.language.placeholder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public final class RavenPlaceholder {

    private final @NotNull String id;
    private final @NotNull Object value;

    public static @NotNull RavenPlaceholder of(@NotNull String id, @NotNull Object value) {
        return new RavenPlaceholder(id, value);
    }

}