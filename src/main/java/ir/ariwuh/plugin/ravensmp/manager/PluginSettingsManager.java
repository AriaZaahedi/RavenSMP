package ir.ariwuh.plugin.ravensmp.manager;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.config.PluginConfigFile;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import ir.ariwuh.plugin.ravensmp.utility.StringUtility;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import net.kyori.adventure.text.format.NamedTextColor;
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

        val defaultNameTagColor = config.getString("settings.general.default-name-tag-color", "gray");
        val globalChatFormat = config.getString("settings.general.global-chat-format", "<display_name><white>: <message>");

        val allowedTeamIdRegex = config.getString("settings.team.allowed-id-regex", "^[a-zA-Z0-9_]+$");
        val maxTeamIdLength = config.getInt("settings.team.id-max-length", 12);
        val maxTeamTagLength = config.getInt("settings.team.tag-max-length", 7);
        val maxTeamMembers = config.getInt("settings.team.max-members", 6);
        val teamCreationCooldownTimeSeconds = config.getInt("settings.team.creation-cooldown-time-seconds", 60);
        val teamInviteExpirationTimeSeconds = config.getInt("settings.team.invite-expiration-time-seconds", 60);

        val teamHomeTeleportCooldownTimeSeconds = config.getInt("settings.team.home.teleport-cooldown-time-seconds", 30);
        val blacklistedTeamHomeWorlds = config.getStringList("settings.team.home.blacklisted-worlds");

        this.pluginSettings.defaultNameTagColor(
                StringUtility.stringToNamedTextColorOrDefault(defaultNameTagColor, NamedTextColor.GRAY)
        );
        this.pluginSettings.globalChatFormat(globalChatFormat);

        this.pluginSettings.allowedTeamIdRegex(allowedTeamIdRegex);
        this.pluginSettings.maxTeamIdLength(maxTeamIdLength);
        this.pluginSettings.maxTeamTagLength(maxTeamTagLength);
        this.pluginSettings.maxTeamMember(maxTeamMembers);
        this.pluginSettings.teamCreationCooldownTimeSeconds(teamCreationCooldownTimeSeconds);
        this.pluginSettings.teamInviteExpirationTimeSeconds(teamInviteExpirationTimeSeconds);

        this.pluginSettings.teamHomeTeleportCooldownTimeSeconds(teamHomeTeleportCooldownTimeSeconds);
        this.pluginSettings.blacklistedTeamHomeWorlds(blacklistedTeamHomeWorlds);
    }

}