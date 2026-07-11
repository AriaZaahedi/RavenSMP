package ir.ariwuh.plugin.ravensmp.manager.team;

import ir.ariwuh.plugin.ravensmp.api.team.status.RavenSMPTeamChangeOptionsStatus;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import ir.ariwuh.plugin.ravensmp.team.SMPTeamOptions;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public final class TeamOptionsManager {

    private final @NotNull PluginSettings pluginSettings;

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
        testOptions.friendlyFire(currentOptions.friendlyFire());
        testOptions.chatMuted(currentOptions.chatMuted());
        testOptions.homeLocation(currentOptions.homeLocation());

        try {
            optionsBuilder.apply(testOptions);
        } catch (Exception exception) {
            return RavenSMPTeamChangeOptionsStatus.UNKNOWN_ERROR;
        }

        val validationStatus = validateTeamOptions(testOptions);
        if (!validationStatus.equals(RavenSMPTeamChangeOptionsStatus.SUCCESSFUL)) return validationStatus;

        currentOptions.tagName(testOptions.tagName());
        currentOptions.tagColor(testOptions.tagColor());
        currentOptions.friendlyFire(testOptions.friendlyFire());
        currentOptions.chatMuted(testOptions.chatMuted());
        currentOptions.homeLocation(testOptions.homeLocation());

        return RavenSMPTeamChangeOptionsStatus.SUCCESSFUL;
    }

    private @NotNull RavenSMPTeamChangeOptionsStatus validateTeamOptions(@NotNull SMPTeamOptions teamOptions) {
        if (!teamOptions.tagName().matches(this.pluginSettings.allowedTeamIdRegex()))
            return RavenSMPTeamChangeOptionsStatus.TAG_NAME_INVALID;
        if (teamOptions.tagColor() == null) return RavenSMPTeamChangeOptionsStatus.TAG_COLOR_INVALID;

        return RavenSMPTeamChangeOptionsStatus.SUCCESSFUL;
    }

}