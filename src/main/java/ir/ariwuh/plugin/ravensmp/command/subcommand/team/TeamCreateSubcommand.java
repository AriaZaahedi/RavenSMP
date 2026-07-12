package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommandHandler;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@RavenSubCommand(label = "create", description = "Create a team.")
public final class TeamCreateSubcommand extends RavenSubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_CREATION_USAGE);
            return;
        }

        val teamId = arguments[0].toLowerCase();
        switch (this.teamManager.createTeam(player, teamId)) {
            case SUCCESSFUL -> RavenMedia.sendMessage(
                    player,
                    RavenLanguagePath.MESSAGE_COMMAND_TEAM_CREATION_SUCCESS,
                    RavenPlaceholderLike.builder().append("team_id", teamId).build()
            );
            case TEAM_ID_EXISTS ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_CREATION_ERROR_EXISTS);
            case TEAM_ID_INVALID ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_INVALID);
            case TEAM_ID_TOO_LONG ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_LONG);
            case PLAYER_HAS_TEAM ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_HAS_TEAM);
            case PLAYER_TEAM_CREATION_COOLDOWN ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_CREATION_ERROR_ON_COOLDOWN);
        }
    }

}