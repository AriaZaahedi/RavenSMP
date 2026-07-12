package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@RavenSubCommand(label = "chat", description = "Send a message to your teammates.")
public final class TeamChatSubcommand extends RavenSubCommandHandler {

    private final @NotNull TeamManager teamManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length == 0) {
            player.sendRichMessage("<red>Usage: /team chat <message>");
            return;
        }

        val playerId = player.getUniqueId();
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM);
            return;
        }

        if (playerTeam.teamOptions().chatMuted() && !playerTeam.isLeader(playerId)) {
            RavenMedia.sendMessage(player, RavenLanguagePath.MESSAGE_COMMAND_TEAM_CHAT_ERROR_MUTED);
            return;
        }

        val message = String.join(" ", arguments);
        val placeholderLike = RavenPlaceholderLike.builder()
                .append("player_name", player.getName())
                .append("message", message)
                .build();

        playerTeam.sendLocalizedMessage(RavenLanguagePath.MESSAGE_COMMAND_TEAM_CHAT_FORMAT, placeholderLike);
    }

}