package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamMember;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@RequiredArgsConstructor
@RavenSubCommand(label = "transfer", description = "Change the team leadership.")
public final class TeamTransferSubcommand extends RavenSubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_TRANSFER_USAGE);
            return;
        }

        val targetPlayerName = arguments[0];
        switch (this.teamManager.transferTeamLeadership(player.getUniqueId(), targetPlayerName)) {
            case PLAYER_LACKING_TEAM ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            case PLAYER_NOT_LEADER ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_NOT_LEADER);
            case TARGET_OFFLINE ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_GENERAL_ERROR_PLAYER_OFFLINE);
            case TARGET_NOT_IN_TEAM ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_LACKING_TEAM);
            case TARGET_IS_SELF ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_SELF);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull Player player, @NonNull @NotNull String[] arguments) {
        if (arguments.length == 1) {
            val playerId = player.getUniqueId();
            val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
            if (playerTeam == null || !playerTeam.isLeader(playerId)) return Collections.emptyList();

            return suggestWithStartingPrefix(
                    playerTeam.teamMembers().stream()
                            .filter(memberId -> !memberId.equals(playerTeam.teamLeader()))
                            .map(RavenSMPTeamMember::playerId)
                            .map(Bukkit::getOfflinePlayer)
                            .map(OfflinePlayer::getName)
                            .filter(Objects::nonNull)
                            .toList(),
                    arguments
            );
        }

        return Collections.emptyList();
    }

}