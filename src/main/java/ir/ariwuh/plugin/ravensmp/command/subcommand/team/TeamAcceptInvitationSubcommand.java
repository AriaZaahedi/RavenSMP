package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommandHandler;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamInvitationManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@RavenSubCommand(label = "accept", description = "Accept a team invitation.")
public final class TeamAcceptInvitationSubcommand extends RavenSubCommandHandler {

    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_USAGE_ACCEPT);
            return;
        }

        val teamId = arguments[0].toLowerCase();
        switch (this.teamInvitationManager.acceptInvitation(teamId, player)) {
            case PLAYER_HAS_TEAM ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_HAS_TEAM);
            case PLAYER_LACKING_INVITE ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_ERROR_LACKING);
            case TEAM_ID_INVALID ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_INVALID);
            case TEAM_INVALID ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_NOT_EXISTS);
            case TEAM_ON_CAPACITY ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_ERROR_ON_CAPACITY);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull Player player, @NotNull String[] arguments) {
        if (arguments.length == 1) return suggestWithStartingPrefix(
                this.teamInvitationManager.pendingTeamNamesByPlayerId(player.getUniqueId()),
                arguments
        );
        return Collections.emptyList();
    }

}