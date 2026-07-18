package ir.ariwuh.plugin.ravensmp.manager;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import ir.ariwuh.plugin.ravensmp.utility.RavenUtility;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath.*;

@RequiredArgsConstructor
public final class WorldManager {

    private final @NotNull RavenSMPPlugin plugin;

    private final @NotNull TeamManager teamManager;

    public void broadcastJoinMessage(@NotNull Player whoJoined) {
        val whoJoinedName = whoJoined.getName();
        RavenUtility.runAsync(() -> this.plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            val onlinePlayerId = onlinePlayer.getUniqueId();
            val serverBroadcastingPrefix = RavenMedia.findValueByPath(
                    onlinePlayerId, BROADCAST_SERVER_GENERAL_PREFIX
            ).asText();
            val teamBroadcastingPrefix = RavenMedia.findValueByPath(
                    onlinePlayerId, BROADCAST_TEAM_GENERAL_PREFIX
            ).asText();
            if (isTeammate(whoJoined.getUniqueId(), onlinePlayerId)) RavenMedia.sendMessage(
                    onlinePlayer,
                    BROADCAST_TEAM_GENERAL_PLAYER_JOIN,
                    RavenPlaceholderLike.builder()
                            .append("team_broadcast_prefix", teamBroadcastingPrefix)
                            .append("player_name", whoJoinedName)
                            .build()
            );
            else RavenMedia.sendMessage(
                    onlinePlayer,
                    BROADCAST_SERVER_GENERAL_PLAYER_JOIN,
                    RavenPlaceholderLike.builder()
                            .append("server_broadcast_prefix", serverBroadcastingPrefix)
                            .append("player_name", whoJoinedName)
                            .build()
            );

        }));
    }

    public void broadcastLeaveMessage(@NotNull Player whoLeft) {
        val whoJoinedName = whoLeft.getName();
        RavenUtility.runAsync(() -> this.plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
            val onlinePlayerId = onlinePlayer.getUniqueId();
            val serverBroadcastingPrefix = RavenMedia.findValueByPath(
                    onlinePlayerId, BROADCAST_SERVER_GENERAL_PREFIX
            ).asText();
            val teamBroadcastingPrefix = RavenMedia.findValueByPath(
                    onlinePlayerId, BROADCAST_TEAM_GENERAL_PREFIX
            ).asText();
            if (isTeammate(whoLeft.getUniqueId(), onlinePlayerId)) RavenMedia.sendMessage(
                    onlinePlayer,
                    BROADCAST_TEAM_GENERAL_PLAYER_LEAVE,
                    RavenPlaceholderLike.builder()
                            .append("team_broadcast_prefix", teamBroadcastingPrefix)
                            .append("player_name", whoJoinedName)
                            .build()
            );
            else RavenMedia.sendMessage(
                    onlinePlayer,
                    BROADCAST_SERVER_GENERAL_PLAYER_LEAVE,
                    RavenPlaceholderLike.builder()
                            .append("server_broadcast_prefix", serverBroadcastingPrefix)
                            .append("player_name", whoJoinedName)
                            .build()
            );

        }));
    }

    private boolean isTeammate(@NotNull UUID playerId, @NotNull UUID targetId) {
        if (playerId.equals(targetId)) return false;

        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) return false;

        return playerTeam.isMember(targetId);
    }

}