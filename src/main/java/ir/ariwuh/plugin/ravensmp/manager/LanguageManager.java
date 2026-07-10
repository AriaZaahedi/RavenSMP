package ir.ariwuh.plugin.ravensmp.manager;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.config.PluginConfigFile;
import ir.ariwuh.plugin.ravensmp.api.language.Language;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

@Accessors(fluent = true)
public final class LanguageManager {

    private final @NotNull RavenSMPPlugin plugin;
    private final @NotNull Logger logger;

    private final @NotNull File languagesFolder;

    private final @NotNull HashSet<Language> languages;
    private final @NotNull HashMap<UUID, Language> playerLanguageMap;

    @Getter
    private @Nullable Language defaultLanguage;

    public LanguageManager(@NotNull RavenSMPPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        this.languages = new HashSet<>();
        this.playerLanguageMap = new HashMap<>();

        this.languagesFolder = new File(plugin.getDataFolder(), "languages");
    }

    public void reloadLanguages() {
        if (!ensureLanguagesFolderExists()) {
            this.logger.severe("[Config] [ERROR] Failed to create languages folder. Disabling plugin...");
            this.plugin.disablePlugin();
            return;
        }

        unloadLanguages();

        val languageFiles = this.languagesFolder.listFiles();
        if (languageFiles == null) return;

        if (languageFiles.length == 0) {
            createEnglishLanguage();
            return;
        }

        Arrays.stream(languageFiles).forEach(this::loadLanguageFile);
        this.languages.forEach(Language::loadConfigCache);

        ensureDefaultLanguage();
    }

    public void unloadLanguages() {
        this.languages.forEach(Language::clearConfigCache);
        this.languages.clear();
        this.playerLanguageMap.clear();
        this.defaultLanguage = null;
    }

    private boolean ensureLanguagesFolderExists() {
        if (this.languagesFolder.exists()) return true;
        return languagesFolder.mkdir();
    }

    private void loadLanguageFile(@NotNull File languageFile) {
        if (!languageFile.getName().endsWith(".yml")) return;

        val languageId = languageFile.getName().replace(".yml", "").toLowerCase();
        val configFile = new PluginConfigFile(languageFile);
        configFile.load();

        val config = configFile.config();

        val displayName = config.getString("language-settings.display-name");
        if (displayName == null) {
            this.logger.warning(
                    "[Language] [ERROR] Skip loading language '%s'. Language's display-name wasn't set properly."
                            .formatted(languageId)
            );
            return;
        }

        val language = new Language(languageId, displayName, config);

        if (config.getBoolean("language-settings.default")) setAsDefaultLanguage(language);

        this.languages.add(language);
    }

    private void setAsDefaultLanguage(@NotNull Language language) {
        if (this.defaultLanguage != null) {
            this.logger.warning(
                    ("[Language] [ERROR] Another default language (%s) was set before! " +
                            "Language '%s' could not be set as default.")
                            .formatted(this.defaultLanguage.id(), language.id())
            );
            return;
        }

        language.defaultLanguage(true);
        this.defaultLanguage = language;
    }

    private void ensureDefaultLanguage() {
        if (this.defaultLanguage != null) return;

        if (this.languages.isEmpty()) {
            this.logger.severe(
                    "[Language] [ERROR] No languages were found to set as default. Disabling plugin..."
            );
            this.plugin.disablePlugin();
            return;
        }

        this.logger.warning(
                "[Language] [WARN] No default language was found! Using the first language as default."
        );

        val firstLanguage = this.languages.iterator().next();
        firstLanguage.defaultLanguage(true);
        this.defaultLanguage = firstLanguage;
    }

    private void createEnglishLanguage() {
        val englishFile = new File(this.languagesFolder, "english.yml");
        if (englishFile.exists()) return;

        this.logger.info("[Language] No languages were found. Adding English as default...");

        val englishConfigFile = new PluginConfigFile(englishFile);
        if (!englishConfigFile.copyFromResource(englishFile, "english", _ -> {
            this.logger.severe(
                    "[Language] [ERROR] Failed to copy default language file. Disabling plugin..."
            );
            this.plugin.disablePlugin();
        })) {
            return;
        }

        englishConfigFile.load();

        val englishLanguage = new Language(
                "english",
                "English",
                englishConfigFile.config()
        );

        englishLanguage.defaultLanguage(true);
        this.defaultLanguage = englishLanguage;
        this.languages.add(englishLanguage);

        englishLanguage.loadConfigCache();
    }

    public void unloadPlayerLanguage(@NotNull UUID playerId) {
        this.playerLanguageMap.remove(playerId);
    }

    @Contract(pure = true)
    public boolean updatePlayerLanguage(@NotNull UUID playerId, @NotNull Language language) {
        if (this.playerLanguageMap.getOrDefault(playerId, this.defaultLanguage).equals(language)) return false;
        this.playerLanguageMap.put(playerId, language);
        return true;
    }

    @Contract(pure = true)
    public @Nullable Language findLanguageById(@NotNull String languageId) {
        return this.languages.stream()
                .filter(language -> language.id().equalsIgnoreCase(languageId))
                .findFirst()
                .orElse(null);
    }

    @Contract(pure = true)
    public @NotNull Language findLanguageByPlayerId(@NotNull UUID playerId) {
        return this.playerLanguageMap.getOrDefault(playerId, this.defaultLanguage);
    }

    @Contract(pure = true)
    public @NotNull Collection<Language> languages() {
        return List.copyOf(this.languages);
    }

}