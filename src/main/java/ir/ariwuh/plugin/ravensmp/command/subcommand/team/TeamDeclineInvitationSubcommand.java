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
@SubCommand(label = "decline", description = "Decline a team invitation.")
public final class TeamDeclineInvitationSubcommand extends SubCommandHandler {

    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team decline <teamName>");
            return;
        }

        val teamId = arguments[0].toLowerCase();
        val playerId = player.getUniqueId();
        switch (this.teamInvitationManager.declineInvitation(teamId, playerId)) {
            case PLAYER_LACKING_INVITE -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_INVITATION_ERROR_LACKING");
            case TEAM_ID_INVALID -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_INVALID");
            case TEAM_INVALID -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_NOT_EXISTS");
            case SUCCESSFUL -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_INVITATION_DECLINED");
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