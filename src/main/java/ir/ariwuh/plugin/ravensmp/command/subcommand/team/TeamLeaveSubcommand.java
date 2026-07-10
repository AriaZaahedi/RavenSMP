package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.SubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.SubCommandHandler;
import ir.ariwuh.plugin.ravensmp.api.language.LanguagePath;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "leave", description = "Leave the team.")
public final class TeamLeaveSubcommand extends SubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        switch (this.teamManager.leaveTeam(player.getUniqueId(), player.getName())) {
            case PLAYER_LACKING_TEAM ->
                    RavenMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            case PLAYER_IS_LEADER ->
                    RavenMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_LEAVE_ERROR_IS_LEADER);
        }
    }

}