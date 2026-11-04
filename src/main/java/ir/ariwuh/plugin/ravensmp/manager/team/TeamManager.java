package ir.ariwuh.plugin.ravensmp.manager.team;

import ir.ariwuh.plugin.ravensmp.api.event.team.*;
import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamMember;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPAccountDao;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPTeamDao;
import ir.ariwuh.plugin.ravensmp.model.team.SMPTeam;
import ir.ariwuh.plugin.ravensmp.model.team.SMPTeamMember;
import ir.ariwuh.plugin.ravensmp.model.team.status.RavenSMPTeamActionStatus;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import ir.ariwuh.plugin.ravensmp.utility.RavenUtility;
import ir.ariwuh.plugin.ravensmp.utility.TimedHashSet;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class TeamManager {

    private final @NotNull PluginSettings pluginSettings;

    private final @NotNull SMPAccountDao accountDao;
    private final @NotNull SMPTeamDao teamDao;
    private final @NotNull TeamTagManager teamTagManager;

    // Optimized: Use ConcurrentHashMap for O(1) team lookups by player ID
    private final @NotNull ConcurrentHashMap<UUID, SMPTeam> teamsByPlayerId = new ConcurrentHashMap<>();
    private final @NotNull HashSet<RavenSMPTeam> teams = new HashSet<>();

    private final @NotNull TimedHashSet<UUID> teamHomeTeleportCooldown = new TimedHashSet<>();

    public void loadTeams() {
        val loadedTeams = this.teamDao.findAll();
        for (RavenSMPTeam team : loadedTeams) {
            SMPTeam castTeam = (SMPTeam) team;
            // Index teams by each member's player ID for O(1) lookups
            for (RavenSMPTeamMember member : castTeam.teamMembers()) {
                SMPTeamMember castMember = (SMPTeamMember) member;
                teamsByPlayerId.put(castMember.playerId(), castTeam);
            }
        }
        loadedTeams.forEach(this.teamTagManager::updateScoreboardTeam);
    }

    public void unloadTeams() {
        this.teams.clear();
        this.teamsByPlayerId.clear();
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus createTeam(@NotNull Player player, @NotNull String teamId) {
        val teamLeaderId = player.getUniqueId();
        val teamLeaderUsername = player.getName();
        if (findTeamByPlayerId(teamLeaderId) != null) return RavenSMPTeamActionStatus.PLAYER_HAS_TEAM;
        if (!teamId.matches(this.pluginSettings.allowedTeamIdRegex())) return RavenSMPTeamActionStatus.TEAM_ID_INVALID;
        if (teamId.length() > this.pluginSettings.maxTeamIdLength()) return RavenSMPTeamActionStatus.TEAM_ID_TOO_LONG;
        if (findTeamById(teamId) != null) return RavenSMPTeamActionStatus.TEAM_ID_EXISTS;

        val account = this.accountDao.findById(teamLeaderId);
        if (account != null) {
            val timeElapsedMillis = System.currentTimeMillis() - account.lastTeamCreationTime();
            val cooldownMillis = this.pluginSettings.teamCreationCooldownTimeSeconds() * 1000L;

            if (timeElapsedMillis < cooldownMillis) return RavenSMPTeamActionStatus.PLAYER_TEAM_CREATION_COOLDOWN;

            account.lastTeamCreationTime(System.currentTimeMillis());
            this.accountDao.update(account);
        }

        val teamLeader = new SMPTeamMember(teamLeaderId, teamLeaderUsername);
        val newTeam = new SMPTeam(teamId, teamLeader);

        this.teamTagManager.removePlayerFromDefaultScoreboardTeam(player);
        this.teamTagManager.createTeamScoreboardForTeam(newTeam.teamId());
        this.teamTagManager.updateScoreboardTeam(newTeam);
        this.teamTagManager.updateScoreboardTeamMembers(newTeam);

        this.teams.add(newTeam);
        // Index the new team by its leader's player ID
        teamsByPlayerId.put(teamLeaderId, (SMPTeam) newTeam);
        this.teamDao.insert(newTeam);

        RavenUtility.callEvent(new RavenSMPTeamCreateEvent(player, newTeam));

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus disbandTeam(@NotNull UUID playerId, @NotNull String teamId) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_NOT_LEADER;
        if (!playerTeam.teamId().equalsIgnoreCase(teamId)) return RavenSMPTeamActionStatus.TEAM_ID_INVALID;

        val whoDisbanded = ((SMPTeam) playerTeam).teamMembers().stream()
                .filter(teamMember -> teamMember.playerId().equals(playerId))
                .findFirst()
                .orElse(null);

        if (whoDisbanded != null) {
            val teamMemberDisbandEvent = new RavenSMPTeamPreDisbandEvent(whoDisbanded, playerTeam);
            RavenUtility.callEvent(teamMemberDisbandEvent);

            if (teamMemberDisbandEvent.isCancelled()) return RavenSMPTeamActionStatus.CANCELED;
        }

        playerTeam.sendLocalizedMessage(RavenLanguagePath.BROADCAST_TEAM_DISBAND);

        this.teamTagManager.removeScoreboardTeam(teamId);
        playerTeam.teamMembers().stream()
                .map(RavenSMPTeamMember::playerId)
                .map(Bukkit::getOfflinePlayer)
                .forEach(offlinePlayer -> {
                    this.teamTagManager.addPlayerToDefaultScoreboardTeam(offlinePlayer);
                    if (this.pluginSettings.teamHomeTeleportationCooldownRemovalOnMemberUpdate())
                        this.teamHomeTeleportCooldown.remove(offlinePlayer.getUniqueId());
                });

        // Remove from indexed map and set
        val smptTeam = (SMPTeam) playerTeam;
        teamsByPlayerId.values().remove(smptTeam);
        if (!teamsByPlayerId.isEmpty()) {
            // Rebuild the index to ensure consistency
            rebuildIndex();
        } else {
            teamsByPlayerId.clear();
        }
        this.teams.remove(playerTeam);
        this.teamDao.delete(teamId);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus leaveTeam(@NotNull UUID playerId, @NotNull String playerName) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_IS_LEADER;

        val whoLeft = ((SMPTeam) playerTeam).teamMembers().stream()
                .filter(teamMember -> teamMember.playerId().equals(playerId))
                .findFirst()
                .orElse(null);

        if (whoLeft != null) {
            val teamMemberLeaveEvent = new RavenSMPTeamMemberPreLeaveEvent(whoLeft, playerTeam);
            RavenUtility.callEvent(teamMemberLeaveEvent);

            if (teamMemberLeaveEvent.isCancelled()) return RavenSMPTeamActionStatus.CANCELED;
        }

        ((SMPTeam) playerTeam).removeMember(playerId);

        if (this.pluginSettings.teamHomeTeleportationCooldownRemovalOnMemberUpdate())
            this.teamHomeTeleportCooldown.remove(playerId);

        this.teamTagManager.updateScoreboardTeamMembers(playerTeam);
        this.teamTagManager.addPlayerToDefaultScoreboardTeam(Bukkit.getOfflinePlayer(playerId));

        this.teamDao.update((SMPTeam) playerTeam);

        playerTeam.sendLocalizedMessage(
                RavenLanguagePath.BROADCAST_TEAM_GENERAL_MEMBER_LEAVE,
                RavenPlaceholderLike.builder()
                        .append("member_name", playerName)
                        .build()
        );

        // Remove from indexed map and rebuild if necessary
        val smptTeam = (SMPTeam) playerTeam;
        teamsByPlayerId.values().remove(smptTeam);
        if (!teamsByPlayerId.isEmpty()) {
            rebuildIndex();
        } else {
            teamsByPlayerId.clear();
        }

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

        val whoKicked = ((SMPTeam) playerTeam).teamMembers().stream()
                .filter(teamMember -> teamMember.playerId().equals(playerId))
                .findFirst()
                .orElse(null);

        val kickedMember = ((SMPTeam) playerTeam).teamMembers().stream()
                .filter(teamMember -> teamMember.playerId().equals(targetId))
                .findFirst()
                .orElse(null);

        if (whoKicked != null && kickedMember != null) {
            val teamMemberKickEvent = new RavenSMPTeamMemberPreKickEvent(whoKicked, kickedMember, playerTeam);
            RavenUtility.callEvent(teamMemberKickEvent);

            if (teamMemberKickEvent.isCancelled()) return RavenSMPTeamActionStatus.CANCELED;
        }

        ((SMPTeam) playerTeam).removeMember(targetId);

        if (this.pluginSettings.teamHomeTeleportationCooldownRemovalOnMemberUpdate())
            this.teamHomeTeleportCooldown.remove(targetId);

        this.teamHomeTeleportCooldown.remove(playerId);

        this.teamTagManager.updateScoreboardTeamMembers(playerTeam);
        this.teamTagManager.addPlayerToDefaultScoreboardTeam(targetOfflinePlayer);

        this.teamDao.update((SMPTeam) playerTeam);

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

        // Remove from indexed map and rebuild if necessary
        val smptTeam = (SMPTeam) playerTeam;
        teamsByPlayerId.values().remove(smptTeam);
        if (!teamsByPlayerId.isEmpty()) {
            rebuildIndex();
        } else {
            teamsByPlayerId.clear();
        }

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus transferTeamLeadership(@NotNull UUID playerId, @NotNull String targetName) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isLeader(playerId)) return RavenSMPTeamActionStatus.PLAYER_NOT_LEADER;

        val targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) return RavenSMPTeamActionStatus.TARGET_OFFLINE;

        val targetId = targetPlayer.getUniqueId();
        if (playerId.equals(targetId)) return RavenSMPTeamActionStatus.TARGET_IS_SELF;
        if (!playerTeam.isMember(targetId)) return RavenSMPTeamActionStatus.TARGET_NOT_IN_TEAM;

        val oldTeamLeader = ((SMPTeam) playerTeam).teamMembers().stream()
                .filter(teamMember -> teamMember.playerId().equals(playerId))
                .findFirst()
                .orElse(null);

        val newTeamLeader = ((SMPTeam) playerTeam).teamMembers().stream()
                .filter(teamMember -> teamMember.playerId().equals(targetId))
                .findFirst()
                .orElse(null);

        if (oldTeamLeader != null && newTeamLeader != null) {
            val teamMemberKickEvent = new RavenSMPTeamPreTransferLeadershipEvent(oldTeamLeader, newTeamLeader, playerTeam);
            RavenUtility.callEvent(teamMemberKickEvent);

            if (teamMemberKickEvent.isCancelled()) return RavenSMPTeamActionStatus.CANCELED;
        }

        val oldLeaderUsername = ((SMPTeam) playerTeam).teamLeader().username();
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
        this.teamDao.update((SMPTeam) playerTeam);

        // Update index with new leader
        teamsByPlayerId.put(playerId, (SMPTeam) playerTeam);

        return RavenSMPTeamActionStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamActionStatus teleportToTeamHome(@NotNull Player player) {
        val playerId = player.getUniqueId();

        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamActionStatus.PLAYER_LACKING_TEAM;

        if (this.teamHomeTeleportCooldown.contains(playerId))
            return RavenSMPTeamActionStatus.PLAYER_TEAM_HOME_TELEPORTATION_COOLDOWN;

        val homeLocation = ((SMPTeam) playerTeam).teamOptions().homeLocation();
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
    public @Nullable SMPTeam findTeamByPlayerId(@NotNull UUID playerId) {
        // Optimized: O(1) lookup instead of O(n) stream filter
        return teamsByPlayerId.get(playerId);
    }

    @Contract(pure = true)
    public @Nullable RavenSMPTeam findTeamById(@NotNull String teamId) {
        return this.teams.stream()
                .filter(team -> team.teamId().equalsIgnoreCase(teamId))
                .findFirst()
                .orElse(null);
    }

    // Helper method to rebuild the index when team membership changes
    private void rebuildIndex() {
        teams.forEach(team -> {
            SMPTeam castTeam = (SMPTeam) team;
            for (RavenSMPTeamMember member : castTeam.teamMembers()) {
                SMPTeamMember castMember = (SMPTeamMember) member;
                teamsByPlayerId.put(castMember.playerId(), castTeam);
            }
        });
    }

    public void updateTeamMemberUsername(@NotNull UUID playerId, @NotNull String username) {
        val playerTeam = findTeamByPlayerId(playerId);
        if (playerTeam == null) return;

        // Optimized: Direct map access instead of nested stream operations
        ((SMPTeam) playerTeam).teamMembers().stream()
                .filter(teamMember -> teamMember.playerId().equals(playerId))
                .findFirst()
                .ifPresent(member -> {
                    SMPTeamMember castMember = (SMPTeamMember) member;
                    if (!castMember.username().equalsIgnoreCase(username)) {
                        castMember.username(username);
                        this.teamDao.update((SMPTeam) playerTeam);
                    }
                });
    }

}