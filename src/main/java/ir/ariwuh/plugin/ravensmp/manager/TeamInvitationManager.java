package ir.ariwuh.plugin.ravensmp.manager;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.api.language.LanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.PlaceholderLike;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.status.RavenSMPTeamInvitationStatus;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import ir.ariwuh.plugin.ravensmp.task.PlayerTeamInvitationTask;
import ir.ariwuh.plugin.ravensmp.team.SMPTeamMember;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class TeamInvitationManager {

    private static final int MAX_TEAM_MEMBERS = 6;

    private final @NotNull RavenSMPPlugin plugin;
    private final @NotNull PluginSettings pluginSettings;
    private final @NotNull TeamManager teamManager;

    private final @NotNull HashMap<RavenSMPTeam, HashSet<UUID>> pendingTeamInvites = new HashMap<>();

    public void removeActivePendingInvites() {
        this.pendingTeamInvites.clear();
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamInvitationStatus invitePlayer(@NotNull UUID playerId,
                                                              @NotNull String targetName) {
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamInvitationStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isLeader(playerId)) return RavenSMPTeamInvitationStatus.PLAYER_NOT_LEADER;
        if (playerTeam.teamMembers().size() >= MAX_TEAM_MEMBERS)
            return RavenSMPTeamInvitationStatus.TEAM_ON_CAPACITY;

        val targetPlayer = Bukkit.getPlayerExact(targetName);
        if (targetPlayer == null) return RavenSMPTeamInvitationStatus.TARGET_OFFLINE;

        val targetUsername = targetPlayer.getName();
        val targetId = targetPlayer.getUniqueId();

        if (playerId.equals(targetId)) return RavenSMPTeamInvitationStatus.TARGET_IS_SELF;

        val targetTeam = this.teamManager.findTeamByPlayerId(targetId);
        if (targetTeam != null) {
            if (targetTeam.equals(playerTeam)) return RavenSMPTeamInvitationStatus.TARGET_IS_IN_TEAM;
            return RavenSMPTeamInvitationStatus.TARGET_IS_IN_ANOTHER_TEAM;
        }

        val targetIsAlreadyInvited = pendingTeamInvitations(playerTeam).contains(targetId);
        if (targetIsAlreadyInvited) return RavenSMPTeamInvitationStatus.TARGET_ALREADY_INVITED;

        pendingTeamInvitations(playerTeam).add(targetId);

        val teamLeader = Bukkit.getPlayer(playerId);
        if (teamLeader != null) {
            playerTeam.sendLocalizedMessage(
                    LanguagePath.BROADCAST_TEAM_INVITATION_MEMBERS,
                    PlaceholderLike.builder()
                            .append("team_leader_name", teamLeader.getName())
                            .append("target_name", targetUsername)
                            .build()
            );
            RavenMedia.sendMessage(
                    targetPlayer,
                    LanguagePath.BROADCAST_TEAM_INVITATION_TARGET,
                    PlaceholderLike.builder()
                            .append("team_leader_name", teamLeader.getName())
                            .append("team_id", playerTeam.teamId())
                            .build()
            );
        }

        new PlayerTeamInvitationTask(
                this.pluginSettings,
                playerTeam, targetId,
                teamInvitationTask -> {
                    if (this.teamManager.findTeamById(playerTeam.teamId()) == null) {
                        teamInvitationTask.cancel();
                        return;
                    }

                    if (!pendingTeamInvitations(playerTeam).contains(targetId)) {
                        teamInvitationTask.cancel();
                        if (teamLeader != null)
                            RavenMedia.sendMessage(
                                    teamLeader,
                                    LanguagePath.MESSAGE_COMMAND_TEAM_INVITATION_ERROR_TARGET_INVITE_DECLINED,
                                    PlaceholderLike.builder()
                                            .append("target_name", targetPlayer.getName())
                                            .build()
                            );
                    }
                },
                () -> {
                    pendingTeamInvitations(playerTeam).remove(targetId);
                    playerTeam.sendLocalizedMessage(
                            LanguagePath.BROADCAST_TEAM_GENERAL_MEMBER_JOIN,
                            PlaceholderLike.builder()
                                    .append("member_name", targetUsername)
                                    .build()
                    );
                },
                () -> {
                    pendingTeamInvitations(playerTeam).remove(targetId);

                    val offlineLeader = Bukkit.getOfflinePlayer(playerId);
                    val offlineLeaderUsername = offlineLeader.getName() != null
                            ? offlineLeader.getName()
                            : "???";
                    if (targetPlayer.isOnline()) RavenMedia.sendMessage(
                            targetPlayer,
                            LanguagePath.BROADCAST_TEAM_INVITATION_EXPIRED_TARGET,
                            PlaceholderLike.builder()
                                    .append("team_leader_name", offlineLeaderUsername)
                                    .build()
                    );

                    if (offlineLeader.getPlayer() != null) {
                        RavenMedia.sendMessage(
                                offlineLeader.getPlayer(),
                                LanguagePath.BROADCAST_TEAM_INVITATION_EXPIRED,
                                PlaceholderLike.builder()
                                        .append("target_name", targetUsername)
                                        .build()
                        );
                    }
                }
        ).runTaskTimerAsynchronously(this.plugin, 0L, 20L);

        return RavenSMPTeamInvitationStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamInvitationStatus acceptInvitation(@NotNull String teamId,
                                                                  @NotNull Player player) {
        if (!teamId.matches(this.pluginSettings.allowedTeamIdRegex()))
            return RavenSMPTeamInvitationStatus.TEAM_ID_INVALID;

        val targetTeam = this.teamManager.findTeamById(teamId);
        if (targetTeam == null) return RavenSMPTeamInvitationStatus.TEAM_INVALID;

        val playerId = player.getUniqueId();

        val pendingTargetTeamInvites = pendingTeamInvitations(targetTeam);
        if (pendingTargetTeamInvites.isEmpty() || !pendingTargetTeamInvites.contains(playerId))
            return RavenSMPTeamInvitationStatus.PLAYER_LACKING_INVITE;

        if (targetTeam.teamMembers().size() >= MAX_TEAM_MEMBERS)
            return RavenSMPTeamInvitationStatus.TEAM_ON_CAPACITY;

        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam != null) return RavenSMPTeamInvitationStatus.PLAYER_HAS_TEAM;

        val teamMember = new SMPTeamMember(playerId, player.getName());
        targetTeam.addMember(teamMember);

        return RavenSMPTeamInvitationStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    public @NotNull RavenSMPTeamInvitationStatus declineInvitation(@NotNull String teamId,
                                                                   @NotNull UUID playerId) {
        if (!teamId.matches(this.pluginSettings.allowedTeamIdRegex()))
            return RavenSMPTeamInvitationStatus.TEAM_ID_INVALID;

        val targetTeam = this.teamManager.findTeamById(teamId);
        if (targetTeam == null) return RavenSMPTeamInvitationStatus.TEAM_INVALID;

        val pendingTargetTeamInvites = pendingTeamInvitations(targetTeam);
        if (pendingTargetTeamInvites.isEmpty() || !pendingTargetTeamInvites.contains(playerId))
            return RavenSMPTeamInvitationStatus.PLAYER_LACKING_INVITE;

        pendingTeamInvitations(targetTeam).remove(playerId);

        return RavenSMPTeamInvitationStatus.SUCCESSFUL;
    }

    @Contract(pure = true)
    private @NotNull HashSet<UUID> pendingTeamInvitations(@NotNull RavenSMPTeam team) {
        return this.pendingTeamInvites.computeIfAbsent(team, _ -> new HashSet<>());
    }

    @Contract(pure = true)
    public @NotNull Collection<String> pendingTeamNamesByPlayerId(@NotNull UUID playerId) {
        return this.pendingTeamInvites.keySet().stream()
                .filter(team -> pendingTeamInvitations(team).contains(playerId))
                .map(RavenSMPTeam::teamId)
                .collect(Collectors.toList());
    }

}