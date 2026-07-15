package ir.ariwuh.plugin.ravensmp.command;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguage;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.command.api.RavenCommand;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPAccountDao;
import ir.ariwuh.plugin.ravensmp.manager.AccountManager;
import ir.ariwuh.plugin.ravensmp.manager.LanguageManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

public final class LanguageCommand extends RavenCommand {

    private final @NotNull RavenSMPPlugin plugin;

    private final @NotNull LanguageManager languageManager;

    private final @NotNull SMPAccountDao accountDao;
    private final @NotNull AccountManager accountManager;

    public LanguageCommand(@NotNull RavenSMPPlugin plugin,
                           @NotNull LanguageManager languageManager,
                           @NotNull SMPAccountDao accountDao, @NotNull AccountManager accountManager) {
        super("language");
        this.plugin = plugin;
        this.languageManager = languageManager;

        this.accountDao = accountDao;
        this.accountManager = accountManager;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] arguments) {
        val playerId = player.getUniqueId();
        if (arguments.length == 0) {
            val separatorFormat = RavenMedia.findValueByPath(
                    playerId,
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

        val account = this.accountManager.findOnlineById(playerId);
        if (account == null) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_GENERAL_ERROR_ACCOUNT_NOT_FOUND);
            return;
        }

        val languageId = arguments[0].toLowerCase();
        val language = this.languageManager.findLanguageById(languageId);
        if (language == null) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_LANGUAGE_ERROR_NOT_FOUND);
            return;
        }

        if (this.languageManager.updatePlayerLanguage(playerId, language)) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_LANGUAGE_PLAYER_UPDATED);
            this.plugin.getServer().getScheduler().runTaskAsynchronously(
                    this.plugin,
                    () -> {
                        account.language(language);
                        accountDao.update(account);
                    }
            );
        } else
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