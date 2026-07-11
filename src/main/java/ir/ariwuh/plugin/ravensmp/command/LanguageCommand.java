package ir.ariwuh.plugin.ravensmp.command;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguage;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.command.api.Command;
import ir.ariwuh.plugin.ravensmp.manager.LanguageManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

public final class LanguageCommand extends Command {

    private final @NotNull LanguageManager languageManager;

    public LanguageCommand(@NotNull LanguageManager languageManager) {
        super("language");
        this.languageManager = languageManager;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 0) {
            val separatorFormat = RavenMedia.findValueByPath(
                    player.getUniqueId(),
                    RavenLanguagePath.MESSAGE_COMMAND_LANGUAGE_USAGE_SEPARATOR_FORMAT
            ).asText();
            val languageIds = this.languageManager.languages().stream()
                    .map(RavenLanguage::id)
                    .toList();
            RavenMedia.sendMessage(
                    player,
                    RavenLanguagePath.MESSAGE_COMMAND_LANGUAGE_USAGE,
                    RavenPlaceholderLike.builder()
                            .append("languages", String.join(separatorFormat, languageIds))
                            .build()
            );
            return;
        }

        val languageId = arguments[0].toLowerCase();
        val language = this.languageManager.findLanguageById(languageId);
        if (language == null) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_LANGUAGE_ERROR_NOT_FOUND);
            return;
        }

        if (this.languageManager.updatePlayerLanguage(player.getUniqueId(), language))
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_LANGUAGE_PLAYER_UPDATED);
        else
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_LANGUAGE_ERROR_ALREADY_SELECTED);
    }

    @Override
    public @NonNull Collection<String> suggest(@NotNull Player player, @NonNull @NotNull String[] arguments) {
        val languageIds = this.languageManager.languages().stream()
                .map(RavenLanguage::id)
                .toList();
        return switch (arguments.length) {
            case 0 -> languageIds;
            case 1 -> suggestWithStartingPrefix(languageIds, arguments);
            default -> List.of();
        };
    }

}