package ir.ariwuh.plugin.ravensmp.command.subcommand.settings;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommandHandler;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPAccountDao;
import ir.ariwuh.plugin.ravensmp.manager.AccountManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@RavenSubCommand(label = "toggleTeamInvites", description = "Toggle team invites.")
public final class SettingsToggleTeamInvitesSubcommand extends RavenSubCommandHandler {

    private final @NotNull RavenSMPPlugin plugin;

    private final @NotNull SMPAccountDao accountDao;
    private final @NotNull AccountManager accountManager;

    @Override
    public void execute(@NotNull Player player, @NotNull String[] arguments) {
        val playerId = player.getUniqueId();
        val account = this.accountManager.findOnlineById(playerId);
        if (account == null) {
            player.sendRichMessage("<red>Failed to find your account. Please rejoin.");
            return;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(
                this.plugin,
                () -> {
                    val accountSettings = account.accountSettings();
                    accountSettings.teamInvitesDisabled(!accountSettings.teamInvitesDisabled());
                    this.accountDao.update(account);
                    val status = RavenMedia.findValueByPath(
                            playerId,
                            accountSettings.teamInvitesDisabled() ?
                                    RavenLanguagePath.MESSAGE_GENERAL_STATUS_DISABLED :
                                    RavenLanguagePath.MESSAGE_GENERAL_STATUS_ENABLED
                    ).asText();

                    RavenMedia.sendMessage(
                            player,
                            RavenLanguagePath.MESSAGE_COMMAND_SETTINGS_TEAM_INVITES_TOGGLED,
                            RavenPlaceholderLike.builder().append("status", status).build()
                    );
                }
        );
    }

}