package ir.ariwuh.plugin.ravensmp.utility;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguageValue;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholder;
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

    public void sendMessage(@NotNull Player player, @NotNull RavenLanguagePath languagePath) {
        val languageValue = findValueByPath(player.getUniqueId(), languagePath);
        player.sendMessage(languageValue.asComponent());
    }

    public void sendMessage(@NotNull Player player,
                            @NotNull RavenLanguagePath languagePath,
                            @NotNull Collection<RavenPlaceholder> placeholders) {
        val languageValue = findValueByPath(player.getUniqueId(), languagePath);
        player.sendMessage(languageValue.asParsedComponent(placeholders));
    }

    @Contract(pure = true)
    public @NotNull RavenLanguageValue findValueByPath(@NotNull UUID playerId,
                                                       @NotNull RavenLanguagePath languagePath) {
        return languageManager.findPlayerLanguageById(playerId)
                .findValueByPath(languagePath);
    }

}