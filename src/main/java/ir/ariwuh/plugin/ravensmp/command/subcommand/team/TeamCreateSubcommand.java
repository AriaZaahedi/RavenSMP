package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.SubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.SubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.TeamManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "create", description = "Create a team.")
public final class TeamCreateSubcommand extends SubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team create <name>");
            return;
        }

        val teamId = arguments[0].toLowerCase();
        switch (this.teamManager.createTeam(player.getUniqueId(), player.getName(), teamId)) {
            case SUCCESSFUL -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_CREATION_SUCCESS");
            case TEAM_ID_EXISTS -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_CREATION_ERROR_EXISTS");
            case TEAM_ID_INVALID -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_INVALID");
            case TEAM_ID_TOO_LONG -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_NAME_LONG");
            case PLAYER_HAS_TEAM -> player.sendRichMessage("MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_HAS_TEAM");
        }
    }

}