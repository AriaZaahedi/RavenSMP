package ir.ariwuh.plugin.ravensmp.impl.adaptor;

import ir.ariwuh.plugin.ravensmp.api.adaptor.RavenLanguageAdaptor;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguage;
import ir.ariwuh.plugin.ravensmp.manager.LanguageManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor
public final class LanguageAdaptor implements RavenLanguageAdaptor {

    private final @NotNull LanguageManager languageManager;

    @Override
    public @Nullable RavenLanguage findLanguageById(@NotNull String languageId) {
        return this.languageManager.findLanguageById(languageId);
    }

    @Override
    public @NotNull RavenLanguage findPlayerLanguageById(@NotNull UUID playerId) {
        return this.languageManager.findLanguageByPlayerId(playerId);
    }

}