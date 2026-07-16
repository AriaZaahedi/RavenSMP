package ir.ariwuh.plugin.ravensmp.api.adaptor;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface RavenLanguageAdaptor {

    @Contract(pure = true)
    @Nullable RavenLanguage findLanguageById(@NotNull String languageId);

    @Contract(pure = true)
    @NotNull RavenLanguage findPlayerLanguageById(@NotNull UUID playerId);

}