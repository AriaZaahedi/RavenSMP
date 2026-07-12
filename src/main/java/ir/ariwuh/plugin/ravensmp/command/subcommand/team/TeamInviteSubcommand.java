package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommandHandler;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamInvitationManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@RavenSubCommand(label = "invite", description = "Invite a player.")
public final class TeamInviteSubcommand extends RavenSubCommandHandler {

    private final @NotNull TeamManager teamManager;
    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_USAGE_INVITE);
            return;
        }

        val targetPlayerName = arguments[0];
        switch (this.teamInvitationManager.invitePlayer(player.getUniqueId(), targetPlayerName)) {
            case PLAYER_LACKING_TEAM ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            case PLAYER_NOT_LEADER ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_NOT_LEADER);
            case TEAM_ON_CAPACITY ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_ERROR_ON_CAPACITY);
            case TARGET_OFFLINE ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_GENERAL_ERROR_PLAYER_OFFLINE);
            case TARGET_IS_IN_TEAM ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_IN_TEAM);
            case TARGET_IS_IN_ANOTHER_TEAM ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_IN_ANOTHER_TEAM);
            case TARGET_IS_SELF ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_SELF);
            case TARGET_ALREADY_INVITED ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_DECLINED);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 1) {
            return suggestWithStartingPrefix(
                    Bukkit.getOnlinePlayers().stream()
                            .filter(onlinePlayer -> this.teamManager
                                    .findTeamByPlayerId(onlinePlayer.getUniqueId()) == null
                            )
                            .map(Player::getName)
                            .toList(),
                    arguments
            );
        }

        return Collections.emptyList();
    }

}