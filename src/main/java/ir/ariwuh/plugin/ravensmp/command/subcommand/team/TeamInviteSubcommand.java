package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.SubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.SubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.TeamInvitationManager;
import ir.ariwuh.plugin.ravensmp.manager.TeamManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@SubCommand(label = "invite", description = "Invite a player.")
public final class TeamInviteSubcommand extends SubCommandHandler {

    private final @NotNull TeamManager teamManager;
    private final @NotNull TeamInvitationManager teamInvitationManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team invite <player>");
            return;
        }

        val targetPlayerName = arguments[0];
        switch (this.teamInvitationManager.invitePlayer(player.getUniqueId(), targetPlayerName)) {
            case PLAYER_LACKING_TEAM ->
                    player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM");
            case PLAYER_NOT_LEADER -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_NOT_LEADER");
            case TEAM_ON_CAPACITY -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_INVITATION_ERROR_ON_CAPACITY");
            case TARGET_OFFLINE -> player.sendRichMessage("MESSAGE_COMMAND_GENERAL_ERROR_PLAYER_OFFLINE");
            case TARGET_IS_IN_TEAM -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_IN_TEAM");
            case TARGET_IS_IN_ANOTHER_TEAM ->
                    player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_IN_ANOTHER_TEAM");
            case TARGET_IS_SELF -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_SELF");
            case TARGET_ALREADY_INVITED -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_INVITATION_DECLINED");
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