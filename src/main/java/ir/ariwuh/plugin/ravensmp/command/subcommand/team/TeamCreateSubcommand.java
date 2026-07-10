package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.SubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.SubCommandHandler;
import ir.ariwuh.plugin.ravensmp.api.language.LanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.PlaceholderLike;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
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
            RavenMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_CREATION_USAGE);
            return;
        }

        val teamId = arguments[0].toLowerCase();
        switch (this.teamManager.createTeam(player.getUniqueId(), player.getName(), teamId)) {
            case SUCCESSFUL -> RavenMedia.sendMessage(
                    player,
                    LanguagePath.MESSAGE_COMMAND_TEAM_CREATION_SUCCESS,
                    PlaceholderLike.builder().append("team_id", teamId).build()
            );
            case TEAM_ID_EXISTS ->
                    RavenMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_CREATION_ERROR_EXISTS);
            case TEAM_ID_INVALID ->
                    RavenMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_INVALID);
            case TEAM_ID_TOO_LONG ->
                    RavenMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_LONG);
            case PLAYER_HAS_TEAM ->
                    RavenMedia.sendMessage(player, LanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_HAS_TEAM);
        }
    }

}