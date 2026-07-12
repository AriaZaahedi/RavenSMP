package ir.ariwuh.plugin.ravensmp.manager.team;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamMember;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

public final class TeamTagManager {

    // Scoreboards are sorted by their name in player-list, "zzz" make it appear to the bottom of that list.
    private static final @NotNull String DEFAULT_SMP_TEAM_SCOREBOARD_NAME = "zzz";
    private static final @NotNull String SMP_TEAM_PREFIX = "smp_";

    private final @NotNull HashMap<String, Team> scoreboardTeams;

    private final @NotNull PluginSettings pluginSettings;

    private final @NotNull Scoreboard scoreboard;
    private final @NotNull Team defaultScoreboardTeam;

    public TeamTagManager(@NotNull PluginSettings pluginSettings) {
        this.pluginSettings = pluginSettings;

        this.scoreboardTeams = new HashMap<>();

        this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        var defaultScoreboardTeam = this.scoreboard.getTeam(DEFAULT_SMP_TEAM_SCOREBOARD_NAME);
        if (defaultScoreboardTeam == null)
            defaultScoreboardTeam = this.scoreboard.registerNewTeam(DEFAULT_SMP_TEAM_SCOREBOARD_NAME);

        this.defaultScoreboardTeam = defaultScoreboardTeam;
        updateDefaultScoreboardTeamColor();
    }

    public @NotNull Team createTeamScoreboardForTeam(@NotNull String teamId) {
        val scoreboardTeamName = SMP_TEAM_PREFIX + teamId;
        var scoreboardTeam = this.scoreboard.getTeam(scoreboardTeamName);
        if (scoreboardTeam == null) scoreboardTeam = this.scoreboard.registerNewTeam(scoreboardTeamName);

        this.scoreboardTeams.putIfAbsent(teamId, scoreboardTeam);
        return scoreboardTeam;
    }

    public void updateScoreboardTeam(@NotNull RavenSMPTeam team) {
        val teamId = team.teamId();
        val scoreboardTeam = findScoreboardTeamByTeamIdElse(
                teamId,
                _ -> createTeamScoreboardForTeam(teamId)
        );

        val teamOptions = team.teamOptions();
        val tagName = teamOptions.tagName();
        val tagColor = teamOptions.tagColor();

        scoreboardTeam.displayName(Component.text(tagName, tagColor));
        scoreboardTeam.prefix(Component.text("[" + tagName + "] ", tagColor));
        scoreboardTeam.color(tagColor);
        scoreboardTeam.setAllowFriendlyFire(teamOptions.friendlyFire());
    }

    public void updateDefaultScoreboardTeamColor() {
        this.defaultScoreboardTeam.color(this.pluginSettings.defaultNameTagColor());
    }

    public void addPlayerToDefaultScoreboardTeam(@NotNull OfflinePlayer player) {
        this.defaultScoreboardTeam.addPlayer(player);
        val onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) onlinePlayer.displayName(
                onlinePlayer.name().color(this.pluginSettings.defaultNameTagColor())
        );
    }

    public void removePlayerFromDefaultScoreboardTeam(@NotNull OfflinePlayer player) {
        this.defaultScoreboardTeam.removePlayer(player);
    }

    public void updateScoreboardTeamMembers(@NotNull RavenSMPTeam team) {
        val scoreboardTeam = findScoreboardTeamByTeamId(team.teamId());
        if (scoreboardTeam == null) return;

        team.teamMembers().stream()
                .map(RavenSMPTeamMember::playerId)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> {
                    removePlayerFromDefaultScoreboardTeam(player);
                    scoreboardTeam.addPlayer(player);
                    val displayName = Component.empty()
                            .append(scoreboardTeam.prefix())
                            .append(player.name())
                            .color(scoreboardTeam.color());
                    player.displayName(displayName);
                });
    }

    public void removeScoreboardTeam(@NotNull String teamId) {
        val scoreboardTeamName = SMP_TEAM_PREFIX + teamId;
        val scoreboardTeam = this.scoreboard.getTeam(scoreboardTeamName);

        if (scoreboardTeam != null) scoreboardTeam.unregister();
        this.scoreboardTeams.remove(teamId);
    }

    public void unregisterScoreboardTeams() {
        this.defaultScoreboardTeam.unregister();
        this.scoreboardTeams.values().forEach(Team::unregister);
        this.scoreboardTeams.clear();
    }

    @Contract(pure = true)
    public @Nullable Team findScoreboardTeamByTeamId(@NotNull String teamId) {
        return this.scoreboardTeams.get(teamId);
    }

    @Contract(pure = true)
    public @NotNull Team findScoreboardTeamByTeamIdElse(@NotNull String teamId,
                                                        @NotNull Function<String, Team> defaultValue) {
        return this.scoreboardTeams.getOrDefault(teamId, defaultValue.apply(teamId));
    }

}