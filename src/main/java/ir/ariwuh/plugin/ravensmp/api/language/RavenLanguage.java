package ir.ariwuh.plugin.ravensmp.api.language;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

@Accessors(fluent = true)
public final class RavenLanguage {

    @Getter
    private final @NotNull String id;
    @Getter
    private final @NotNull String displayName;
    @Getter
    private final @NotNull FileConfiguration config;
    private final @NotNull HashMap<RavenLanguagePath, RavenLanguageValue> languageValuesMap;

    @Getter
    @Setter
    private boolean defaultLanguage;

    public RavenLanguage(@NotNull String id,
                         @NotNull String displayName,
                         @NotNull FileConfiguration config) {
        this.id = id;
        this.displayName = displayName;
        this.config = config;

        this.languageValuesMap = new HashMap<>();
    }

    public void clearConfigCache() {
        this.languageValuesMap.clear();
    }

    public void loadConfigCache() {
        for (val languagePath : RavenLanguagePath.values()) {
            val configValue = this.config.get(languagePath.languagePath());
            if (configValue == null) continue;

            this.languageValuesMap.put(languagePath, new RavenLanguageValue(configValue));
        }
    }

    @Contract(pure = true)
    public @NotNull RavenLanguageValue findValueByPath(@Nullable RavenLanguagePath languagePath) {
        return this.languageValuesMap.getOrDefault(languagePath, RavenLanguageValue.NOT_FOUND_VALUE);
    }

}