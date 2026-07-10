package ir.ariwuh.plugin.ravensmp.manager;

import ir.ariwuh.plugin.ravensmp.api.language.LanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.PlaceholderLike;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.status.RavenSMPTeamActionStatus;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import ir.ariwuh.plugin.ravensmp.team.SMPTeam;
import ir.ariwuh.plugin.ravensmp.team.SMPTeamMember;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;

@RequiredArgsConstructor
public final class TeamManager {

    private final @NotNull PluginSettings pluginSettings;

    private final @NotNull HashSet<RavenSMPTeam> teams = new HashSet<>();

    public void unloadTeams() {
        this.teams.clear();
    }

    public void updateTeamAudience(@NotNull UUID playerId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return;

        ((SMPTeam) playerTeam).updateTeamAudience();
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus createTeam(@NotNull UUID teamLeaderId, @NotNull String teamLeaderUsername,
                                                        @NotNull String teamId) {
        if (findTeamByPlayerId(teamLeaderId) != null) return RavenSMPTeamActionStatus.PLAYER_HAS_TEAM;
        if (!teamId.matches(this.pluginSettings.allowedTeamIdRegex())) return RavenSMPTeamActionStatus.TEAM_ID_INVALID;
        if (teamId.length() > this.pluginSettings.maxTeamIdLength()) return RavenSMPTeamActionStatus.TEAM_ID_TOO_LONG;
        if (findTeamById(teamId) != null) return RavenSMPTeamActionStatus.TEAM_ID_EXISTS;

        val teamLeader = new SMPTeamMember(teamLeaderId, teamLeaderUsername);
        val newTeam = new SMPTeam(teamId, teamLeader);

        this.teams.add(newTeam);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus disbandTeam(@NotNull UUID playerId, @NotNull String teamId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_NOT_LEADER;
        if (!playerTeam.teamId().equalsIgnoreCase(teamId)) return RavenSMPTeamActionStatus.TEAM_ID_INVALID;

        playerTeam.sendLocalizedMessage(LanguagePath.BROADCAST_TEAM_DISBAND);
        this.teams.remove(playerTeam);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus leaveTeam(@NotNull UUID playerId, @NotNull String playerName) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_IS_LEADER;

        playerTeam.removeMember(playerId);
        playerTeam.sendLocalizedMessage(
                LanguagePath.BROADCAST_TEAM_GENERAL_MEMBER_LEAVE,
                PlaceholderLike.builder()
                        .append("member_name", playerName)
                        .build()
        );

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus kickMember(@NotNull UUID playerId, @NotNull String targetName) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_NOT_LEADER;

        val targetOfflinePlayer = Bukkit.getOfflinePlayer(targetName);
        val targetId = targetOfflinePlayer.getUniqueId();

        if (playerId.equals(targetId)) return RavenSMPTeamActionStatus.TARGET_IS_SELF;
        if (!playerTeam.isMember(targetId)) return RavenSMPTeamActionStatus.TARGET_NOT_IN_TEAM;

        playerTeam.removeMember(targetId);

        playerTeam.sendLocalizedMessage(
                LanguagePath.BROADCAST_TEAM_KICK,
                PlaceholderLike.builder()
                        .append("member_name", targetOfflinePlayer.getName() != null ? targetOfflinePlayer.getName() : "???")
                        .build()
        );
        val targetPlayer = targetOfflinePlayer.getPlayer();
        if (targetPlayer != null) RavenMedia.sendMessage(
                targetPlayer,
                LanguagePath.MESSAGE_COMMAND_TEAM_KICK_TARGET
        );

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus transferTeamLeader(@NotNull UUID playerId, @NotNull String targetName) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_NOT_LEADER;

        val targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) return RavenSMPTeamActionStatus.TARGET_OFFLINE;

        val targetId = targetPlayer.getUniqueId();
        if (playerId.equals(targetId)) return RavenSMPTeamActionStatus.TARGET_IS_SELF;
        if (!playerTeam.isMember(targetId)) return RavenSMPTeamActionStatus.TARGET_NOT_IN_TEAM;

        val oldLeaderUsername = playerTeam.teamLeader().username();
        val newLeaderUsername = targetPlayer.getName();

        playerTeam.sendLocalizedMessage(
                LanguagePath.BROADCAST_TEAM_TRANSFER,
                PlaceholderLike.builder()
                        .append("old_leader", oldLeaderUsername)
                        .append("new_leader", newLeaderUsername)
                        .build()
        );

        val newLeader = new SMPTeamMember(targetId, targetPlayer.getName());
        ((SMPTeam) playerTeam).teamLeader(newLeader);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @Nullable RavenSMPTeam findTeamByPlayerId(@NotNull UUID playerId) {
        return this.teams.stream()
                .filter(team -> team.isMember(playerId))
                .findFirst()
                .orElse(null);
    }

    @Contract(pure = true)
    public @Nullable RavenSMPTeam findTeamById(@NotNull String teamId) {
        return this.teams.stream()
                .filter(team -> team.teamId().equalsIgnoreCase(teamId))
                .findFirst()
                .orElse(null);
    }

}