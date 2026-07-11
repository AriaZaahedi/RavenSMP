package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.SubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.SubCommandHandler;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamInvitationManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@SubCommand(label = "decline", description = "Decline a team invitation.")
public final class TeamDeclineInvitationSubcommand extends SubCommandHandler {

    private final @NotNull TeamManager teamManager;
    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_USAGE_DECLINE);
            return;
        }

        val teamId = arguments[0].toLowerCase();
        val playerId = player.getUniqueId();
        val teamLeaderName = teamLeaderNameById(teamId, playerId);
        switch (this.teamInvitationManager.declineInvitation(teamId, playerId)) {
            case PLAYER_LACKING_INVITE ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_ERROR_LACKING);
            case TEAM_ID_INVALID ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_INVALID);
            case TEAM_INVALID ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_NOT_EXISTS);
            case SUCCESSFUL -> RavenMedia.sendMessage(
                    player,
                    RavenLanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_DECLINED,
                    RavenPlaceholderLike.builder().append("host_name", teamLeaderName).build()
            );
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

    private @NotNull String teamLeaderNameById(@NotNull String teamId, @NotNull UUID playerId) {
        return this.teamInvitationManager.pendingTeamNamesByPlayerId(playerId).stream()
                .filter(pendingTeamName -> pendingTeamName.equalsIgnoreCase(teamId))
                .map(this.teamManager::findTeamById)
                .takeWhile(Objects::nonNull)
                .findFirst()
                .map(team -> team.teamLeader().username())
                .orElse("???");
    }

}