package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.SubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.SubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.TeamInvitationManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@SubCommand(label = "accept", description = "Accept a team invitation.")
public final class TeamAcceptInvitationSubcommand extends SubCommandHandler {

    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team accept <teamName>");
            return;
        }

        val teamId = arguments[0].toLowerCase();
        switch (this.teamInvitationManager.acceptInvitation(teamId, player)) {
            case PLAYER_HAS_TEAM -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_HAS_TEAM");
            case PLAYER_LACKING_INVITE -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_INVITATION_ERROR_LACKING");
            case TEAM_ID_INVALID -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_INVALID");
            case TEAM_INVALID -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_NOT_EXISTS");
            case TEAM_ON_CAPACITY -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_INVITATION_ERROR_ON_CAPACITY");
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