package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.SubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.SubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.TeamManager;
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
        switch (this.teamManager.leaveTeam(player.getUniqueId())) {
            case PLAYER_LACKING_TEAM ->
                    player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM");
            case PLAYER_IS_LEADER -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_LEAVE_ERROR_IS_LEADER");
        }
    }

}