package ir.ariwuh.plugin.ravensmp.utility;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.api.language.LanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.LanguageValue;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.Placeholder;
import ir.ariwuh.plugin.ravensmp.manager.LanguageManager;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

@UtilityClass
public final class RavenMedia {

    private final @NotNull LanguageManager languageManager = RavenSMPPlugin.instance().languageManager();

    public void sendMessage(@NotNull Player player, @NotNull LanguagePath languagePath) {
        val languageValue = findValueByPath(player.getUniqueId(), languagePath);
        player.sendMessage(languageValue.asComponent());
    }

    public void sendMessage(@NotNull Player player,
                            @NotNull LanguagePath languagePath,
                            @NotNull Collection<Placeholder> placeholders) {
        val languageValue = findValueByPath(player.getUniqueId(), languagePath);
        player.sendMessage(languageValue.asParsedComponent(placeholders));
    }

    @Contract(pure = true)
    public @NotNull LanguageValue findValueByPath(@NotNull UUID playerId,
                                                  @NotNull LanguagePath languagePath) {
        return languageManager.findLanguageByPlayerId(playerId)
                .findValueByPath(languagePath);
    }

}