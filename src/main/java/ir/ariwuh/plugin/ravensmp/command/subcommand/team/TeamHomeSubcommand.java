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
@RavenSubCommand(label = "home", description = "Teleport to team home.")
public final class TeamHomeSubcommand extends RavenSubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        switch (this.teamManager.teleportToTeamHome(player)) {
            case PLAYER_LACKING_TEAM ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            case PLAYER_TEAM_HOME_TELEPORTATION_COOLDOWN ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_HOME_ERROR_ON_COOLDOWN);
            case PLAYER_TEAM_HOME_NOT_EXIST ->
                    RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_HOME_ERROR_NOT_EXISTS);
        }
    }

}