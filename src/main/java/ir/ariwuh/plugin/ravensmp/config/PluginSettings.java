package ir.ariwuh.plugin.ravensmp.config;

import lombok.Data;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

@Accessors(fluent = true)
@Data
public final class PluginSettings {

    // === Database Settings ===
    private String databaseUri;
    private String databaseName;

    // === General ===
    private NamedTextColor defaultNameTagColor;
    private String globalChatFormat;

    // === Team Settings ===
    private String allowedTeamIdRegex;
    private int maxTeamIdLength;
    private int maxTeamTagLength;
    private int maxTeamMember;
    private int teamCreationCooldownTimeSeconds;
    private int teamInviteExpirationTimeSeconds;

    private int teamHomeTeleportCooldownTimeSeconds;
    private boolean teamHomeTeleportationCooldownRemovalOnMemberUpdate;
    private List<String> blacklistedTeamHomeWorlds;

}