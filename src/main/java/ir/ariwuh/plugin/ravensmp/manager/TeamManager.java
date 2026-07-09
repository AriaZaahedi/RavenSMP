package ir.ariwuh.plugin.ravensmp.manager;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.status.RavenSMPTeamActionStatus;
import ir.ariwuh.plugin.ravensmp.team.SMPTeam;
import ir.ariwuh.plugin.ravensmp.team.SMPTeamMember;
import lombok.val;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;

public final class TeamManager {

    public static final @NotNull String ALLOWED_TEAM_ID_REGEX = "^[a-zA-Z0-9_]+$";
    private static final int MAX_TEAM_ID_LENGTH = 12;

    private final @NotNull HashSet<RavenSMPTeam> teams = new HashSet<>();

    public void unloadTeams() {
        this.teams.clear();
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus createTeam(@NotNull UUID teamLeaderId, @NotNull String teamLeaderUsername,
                                                        @NotNull String teamId) {
        if (findTeamByPlayerId(teamLeaderId) != null) return RavenSMPTeamActionStatus.PLAYER_HAS_TEAM;
        if (!teamId.matches(ALLOWED_TEAM_ID_REGEX)) return RavenSMPTeamActionStatus.TEAM_ID_INVALID;
        if (teamId.length() > MAX_TEAM_ID_LENGTH) return RavenSMPTeamActionStatus.TEAM_ID_TOO_LONG;
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

        this.teams.remove(playerTeam);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus leaveTeam(@NotNull UUID playerId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_IS_LEADER;

        playerTeam.removeMember(playerId);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus kickMember(@NotNull UUID playerId, @NotNull String targetName) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_NOT_LEADER;

        val targetPlayer = Bukkit.getOfflinePlayer(targetName);
        val targetId = targetPlayer.getUniqueId();

        if (playerId.equals(targetId)) return RavenSMPTeamActionStatus.TARGET_IS_SELF;
        if (!playerTeam.isMember(targetId)) return RavenSMPTeamActionStatus.TARGET_NOT_IN_TEAM;

        playerTeam.removeMember(targetId);

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