package ir.ariwuh.plugin.ravensmp.manager;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.config.PluginConfigFile;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
public final class PluginSettingsManager {

    private final @NotNull RavenSMPPlugin plugin;

    private final @NotNull PluginConfigFile pluginSettingsConfigFile;
    @Getter
    private final @NotNull PluginSettings pluginSettings;

    public PluginSettingsManager(@NotNull RavenSMPPlugin plugin) {
        this.plugin = plugin;

        this.pluginSettingsConfigFile = new PluginConfigFile("settings");
        this.pluginSettings = new PluginSettings();
    }

    public void reloadPluginSettings() {
        if (!this.pluginSettingsConfigFile.exists()) if (!this.pluginSettingsConfigFile.copyFromResource(
                "settings",
                _ -> {
                    this.plugin.getLogger().severe(
                            "[Language] [ERROR] Failed to copy default settings file. Disabling plugin..."
                    );
                    this.plugin.disablePlugin();
                }
        )) return;

        this.pluginSettingsConfigFile.load();
        val config = this.pluginSettingsConfigFile.config();

        val allowedTeamIdRegex = config.getString("settings.team.allowed-id-regex", "^[a-zA-Z0-9_]+$");
        val maxTeamIdLength = config.getInt("settings.team.id-max-length", 12);
        val maxTeamMembers = config.getInt("settings.team.max-members", 6);
        val teamCreationCooldownTimeSeconds = config.getInt("settings.team.creation-cooldown-time-seconds", 60);
        val teamInviteExpirationTimeSeconds = config.getInt("settings.team.invite-expiration-time-seconds", 60);

        val teamHomeTeleportCooldownTimeSeconds = config.getInt("settings.team.home.teleport-cooldown-time-seconds", 30);
        val blacklistedTeamHomeWorlds = config.getStringList("settings.team.home.blacklisted-worlds");

        this.pluginSettings.allowedTeamIdRegex(allowedTeamIdRegex);
        this.pluginSettings.maxTeamIdLength(maxTeamIdLength);
        this.pluginSettings.maxTeamMember(maxTeamMembers);
        this.pluginSettings.teamCreationCooldownTimeSeconds(teamCreationCooldownTimeSeconds);
        this.pluginSettings.teamInviteExpirationTimeSeconds(teamInviteExpirationTimeSeconds);

        this.pluginSettings.teamHomeTeleportCooldownTimeSeconds(teamHomeTeleportCooldownTimeSeconds);
        this.pluginSettings.blacklistedTeamHomeWorlds(blacklistedTeamHomeWorlds);
    }

}