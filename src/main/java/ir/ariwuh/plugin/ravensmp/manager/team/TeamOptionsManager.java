package ir.ariwuh.plugin.ravensmp.manager.team;

import ir.ariwuh.plugin.ravensmp.model.team.status.RavenSMPTeamChangeOptionsStatus;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPTeamOptionsDao;
import ir.ariwuh.plugin.ravensmp.model.team.SMPTeamOptions;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public final class TeamOptionsManager {

    private final @NotNull PluginSettings pluginSettings;

    private final @NotNull SMPTeamOptionsDao teamOptionsDao;

    private final @NotNull TeamTagManager teamTagManager;
    private final @NotNull TeamManager teamManager;

    public @NotNull RavenSMPTeamChangeOptionsStatus changeTeamOptions(@NotNull UUID playerId,
                                                                      @NotNull SMPTeamOptions.Builder optionsBuilder) {
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) return RavenSMPTeamChangeOptionsStatus.PLAYER_LACKING_TEAM;
        if (!playerTeam.isLeader(playerId)) return RavenSMPTeamChangeOptionsStatus.PLAYER_NOT_LEADER;

        val currentOptions = (SMPTeamOptions) playerTeam.teamOptions();
        val testOptions = new SMPTeamOptions(playerTeam);
        testOptions.tagName(currentOptions.tagName());
        testOptions.tagColor(currentOptions.tagColor());
        testOptions.homeLocation(currentOptions.homeLocation());
        testOptions.friendlyFire(currentOptions.friendlyFire());
        testOptions.chatMuted(currentOptions.chatMuted());

        try {
            optionsBuilder.apply(testOptions);
        } catch (Exception exception) {
            return RavenSMPTeamChangeOptionsStatus.UNKNOWN_ERROR;
        }

        val validationStatus = validateTeamOptions(testOptions);
        if (!validationStatus.equals(RavenSMPTeamChangeOptionsStatus.SUCCESSFUL)) return validationStatus;

        currentOptions.tagName(testOptions.tagName());
        currentOptions.tagColor(testOptions.tagColor());
        currentOptions.homeLocation(testOptions.homeLocation());
        currentOptions.friendlyFire(testOptions.friendlyFire());
        currentOptions.chatMuted(testOptions.chatMuted());

        this.teamTagManager.updateScoreboardTeam(playerTeam);
        this.teamTagManager.updateScoreboardTeamMembers(playerTeam);

        this.teamOptionsDao.update(currentOptions);

        return RavenSMPTeamChangeOptionsStatus.SUCCESSFUL;
    }

    private boolean isInBlacklistedWorld(@NotNull World world) {
        return this.pluginSettings.blacklistedTeamHomeWorlds().stream()
                .map(Bukkit::getWorld)
                .filter(Objects::nonNull)
                .anyMatch(blacklistedWorld -> blacklistedWorld.getName().equals(world.getName()));
    }

    private @NotNull RavenSMPTeamChangeOptionsStatus validateTeamOptions(@NotNull SMPTeamOptions teamOptions) {
        val playerTeam = teamOptions.smpTeam();

        if (!teamOptions.tagName().matches(this.pluginSettings.allowedTeamIdRegex()))
            return RavenSMPTeamChangeOptionsStatus.TAG_NAME_INVALID;
        if (teamOptions.tagName().length() > this.pluginSettings.maxTeamTagLength())
            return RavenSMPTeamChangeOptionsStatus.TAG_NAME_LONG;
        if (teamOptions.tagColor() == null) return RavenSMPTeamChangeOptionsStatus.TAG_COLOR_INVALID;

        val homeLocation = teamOptions.homeLocation();
        if (homeLocation != null)
            if (isInBlacklistedWorld(homeLocation.getWorld()))
                return RavenSMPTeamChangeOptionsStatus.HOME_WORLD_BLACKLISTED;

        if (homeLocation == null) this.teamManager.removeTeamHomeCooldownForTeam(playerTeam);

        return RavenSMPTeamChangeOptionsStatus.SUCCESSFUL;
    }

}