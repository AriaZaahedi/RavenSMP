package ir.ariwuh.plugin.ravensmp.manager.team;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamMember;
import ir.ariwuh.plugin.ravensmp.api.team.status.RavenSMPTeamActionStatus;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import ir.ariwuh.plugin.ravensmp.team.SMPTeam;
import ir.ariwuh.plugin.ravensmp.team.SMPTeamMember;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import ir.ariwuh.plugin.ravensmp.utility.TimedHashSet;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class TeamManager {

    private final @NotNull PluginSettings pluginSettings;
    private final @NotNull TeamTagManager teamTagManager;

    private final @NotNull HashSet<RavenSMPTeam> teams = new HashSet<>();
    private final @NotNull TimedHashSet<UUID> teamCreationCooldown = new TimedHashSet<>();
    private final @NotNull TimedHashSet<UUID> teamHomeTeleportCooldown = new TimedHashSet<>();

    public void unloadTeams() {
        this.teams.clear();
    }

    public void updateTeamAudience(@NotNull UUID playerId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return;

        ((SMPTeam) playerTeam).updateTeamAudience();
    }

    public void removeTeamHomeCooldownForTeam(@NotNull RavenSMPTeam playerTeam) {
        val teamMemberIds = playerTeam.teamMembers().stream()
                .map(RavenSMPTeamMember::playerId)
                .filter(this.teamHomeTeleportCooldown::contains)
                .collect(Collectors.toCollection(ArrayList::new));

        teamMemberIds.forEach(this.teamHomeTeleportCooldown::remove);
    }

    public void updateScoreboardTeamFor(@NotNull UUID playerId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) {
            this.teamTagManager.addPlayerToDefaultScoreboardTeam(Bukkit.getOfflinePlayer(playerId));
            return;
        }

        this.teamTagManager.updateScoreboardTeamMembers(playerTeam);
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus createTeam(@NotNull Player player, @NotNull String teamId) {
        val teamLeaderId = player.getUniqueId();
        val teamLeaderUsername = player.getName();
        if (findTeamByPlayerId(teamLeaderId) != null) return RavenSMPTeamActionStatus.PLAYER_HAS_TEAM;
        if (!teamId.matches(this.pluginSettings.allowedTeamIdRegex())) return RavenSMPTeamActionStatus.TEAM_ID_INVALID;
        if (teamId.length() > this.pluginSettings.maxTeamIdLength()) return RavenSMPTeamActionStatus.TEAM_ID_TOO_LONG;
        if (findTeamById(teamId) != null) return RavenSMPTeamActionStatus.TEAM_ID_EXISTS;
        if (this.teamCreationCooldown.contains(teamLeaderId))
            return RavenSMPTeamActionStatus.PLAYER_TEAM_CREATION_COOLDOWN;

        this.teamCreationCooldown.add(
                teamLeaderId,
                this.pluginSettings.teamCreationCooldownTimeSeconds(),
                TimeUnit.SECONDS
        );
        val teamLeader = new SMPTeamMember(teamLeaderId, teamLeaderUsername);
        val newTeam = new SMPTeam(teamId, teamLeader);

        this.teamTagManager.removePlayerFromDefaultScoreboardTeam(player);
        this.teamTagManager.createTeamScoreboardForTeam(newTeam.teamId());
        this.teamTagManager.updateScoreboardTeam(newTeam);
        this.teamTagManager.updateScoreboardTeamMembers(newTeam);

        this.teams.add(newTeam);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus disbandTeam(@NotNull UUID playerId, @NotNull String teamId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_NOT_LEADER;
        if (!playerTeam.teamId().equalsIgnoreCase(teamId)) return RavenSMPTeamActionStatus.TEAM_ID_INVALID;

        playerTeam.sendLocalizedMessage(RavenLanguagePath.BROADCAST_TEAM_DISBAND);

        this.teamTagManager.removeScoreboardTeam(teamId);
        playerTeam.teamMembers().stream()
                .map(RavenSMPTeamMember::playerId)
                .map(Bukkit::getOfflinePlayer)
                .forEach(this.teamTagManager::addPlayerToDefaultScoreboardTeam);

        this.teams.remove(playerTeam);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus leaveTeam(@NotNull UUID playerId, @NotNull String playerName) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_IS_LEADER;

        playerTeam.removeMember(playerId);

        this.teamTagManager.updateScoreboardTeamMembers(playerTeam);
        this.teamTagManager.addPlayerToDefaultScoreboardTeam(Bukkit.getOfflinePlayer(playerId));

        playerTeam.sendLocalizedMessage(
                RavenLanguagePath.BROADCAST_TEAM_GENERAL_MEMBER_LEAVE,
                RavenPlaceholderLike.builder()
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

        this.teamTagManager.updateScoreboardTeamMembers(playerTeam);
        this.teamTagManager.addPlayerToDefaultScoreboardTeam(targetOfflinePlayer);

        playerTeam.sendLocalizedMessage(
                RavenLanguagePath.BROADCAST_TEAM_KICK,
                RavenPlaceholderLike.builder()
                        .append("member_name", targetOfflinePlayer.getName() != null ? targetOfflinePlayer.getName() : "???")
                        .build()
        );
        val targetPlayer = targetOfflinePlayer.getPlayer();
        if (targetPlayer != null) RavenMedia.sendMessage(
                targetPlayer,
                RavenLanguagePath.MESSAGE_COMMAND_TEAM_KICK_TARGET
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
                RavenLanguagePath.BROADCAST_TEAM_TRANSFER,
                RavenPlaceholderLike.builder()
                        .append("old_leader", oldLeaderUsername)
                        .append("new_leader", newLeaderUsername)
                        .build()
        );

        val newLeader = new SMPTeamMember(targetId, targetPlayer.getName());
        ((SMPTeam) playerTeam).teamLeader(newLeader);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus teleportToTeamHome(@NotNull Player player) {
        val playerId = player.getUniqueId();

        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;

        if (this.teamHomeTeleportCooldown.contains(playerId))
            return RavenSMPTeamActionStatus.PLAYER_TEAM_HOME_TELEPORTATION_COOLDOWN;

        val homeLocation = playerTeam.teamOptions().homeLocation();
        if (homeLocation == null) return RavenSMPTeamActionStatus.PLAYER_TEAM_HOME_NOT_EXIST;

        this.teamHomeTeleportCooldown.add(
                playerId,
                this.pluginSettings.teamHomeTeleportCooldownTimeSeconds(),
                TimeUnit.SECONDS
        );
        player.teleportAsync(homeLocation);

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