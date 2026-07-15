package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@RavenSubCommand(label = "leave", description = "Leave the team.")
public final class TeamLeaveSubcommand extends RavenSubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        switch (this.teamManager.leaveTeam(player.getUniqueId(), player.getName())) {
            case SUCCESSFUL -> RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_LEAVE_SUCCESS);
            case PLAYER_LACKING_TEAM ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            case PLAYER_IS_LEADER ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_LEAVE_ERROR_IS_LEADER);
        }
    }

}