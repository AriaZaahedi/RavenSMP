package ir.ariwuh.plugin.ravensmp.command.subcommand.team;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholder;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholderLike;
import ir.ariwuh.plugin.ravensmp.team.status.RavenSMPTeamChangeOptionsStatus;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamOptionsManager;
import ir.ariwuh.plugin.ravensmp.team.SMPTeamOptions;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import ir.ariwuh.plugin.ravensmp.utility.StringUtility;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;

import static ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath.*;
import static ir.ariwuh.plugin.ravensmp.team.status.RavenSMPTeamChangeOptionsStatus.*;

@RavenSubCommand(label = "options", description = "Adjust team options.")
public final class TeamOptionsSubcommand extends RavenSubCommandHandler {

    private static final @NotNull List<String> TAG_COLORS = List.of(
            "dark_blue", "dark_green", "dark_aqua",
            "dark_red", "dark_purple", "dark_gray",
            "light_purple", "gold", "gray", "blue", "green",
            "aqua", "red", "yellow", "white", "black"
    );

    private final @NotNull TeamManager teamManager;
    private final @NotNull TeamOptionsManager teamOptionsManager;

    private final @NotNull Map<String, TeamOptionSetter> optionSetters;
    private final @NotNull Map<String, RavenLanguagePath> broadcastMessages;
    private final @NotNull Map<RavenSMPTeamChangeOptionsStatus, RavenLanguagePath> errorMessages;

    @SuppressWarnings("SpellCheckingInspection")
    public TeamOptionsSubcommand(@NotNull TeamManager teamManager,
                                 @NotNull TeamOptionsManager teamOptionsManager) {
        this.teamManager = teamManager;
        this.teamOptionsManager = teamOptionsManager;

        this.optionSetters = Map.ofEntries(
                Map.entry("tagname", (options, _, value) -> options.tagName(value)),
                Map.entry("tagcolor", (options, _, value) -> options.tagColor(StringUtility.stringToNamedTextColor(value))),
                Map.entry("friendlyfire", (options, _, value) -> options.friendlyFire(Boolean.parseBoolean(value))),
                Map.entry("chatmuted", (options, _, value) -> options.chatMuted(Boolean.parseBoolean(value))),
                Map.entry("sethome", (options, player, value) ->
                        options.homeLocation(value.equalsIgnoreCase("remove") ? null : player.getLocation()))
        );

        this.broadcastMessages = Map.ofEntries(
                Map.entry("tagname", BROADCAST_TEAM_OPTION_TAG_NAME_CHANGED),
                Map.entry("tagcolor", BROADCAST_TEAM_OPTION_TAG_COLOR_CHANGED),
                Map.entry("friendlyfire", BROADCAST_TEAM_OPTION_FRIENDLY_FIRE_TOGGLED),
                Map.entry("chatmuted", BROADCAST_TEAM_OPTION_CHAT_TOGGLED),
                Map.entry("sethome", BROADCAST_TEAM_OPTION_HOME_UPDATED)
        );

        this.errorMessages = Map.ofEntries(
                Map.entry(PLAYER_LACKING_TEAM, MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM),
                Map.entry(PLAYER_NOT_LEADER, MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_NOT_LEADER),
                Map.entry(TAG_NAME_INVALID, MESSAGE_COMMAND_TEAM_GENERAL_TAG_ERROR_INVALID_NAME),
                Map.entry(TAG_NAME_LONG, MESSAGE_COMMAND_TEAM_GENERAL_TAG_ERROR_LONG),
                Map.entry(TAG_COLOR_INVALID, MESSAGE_COMMAND_TEAM_GENERAL_TAG_ERROR_INVALID_COLOR),
                Map.entry(HOME_WORLD_BLACKLISTED, MESSAGE_COMMAND_TEAM_OPTIONS_ERROR_HOME_BLACKLISTED_WORLD),
                Map.entry(UNKNOWN_ERROR, MESSAGE_COMMAND_TEAM_OPTIONS_ERROR_UNKNOWN)
        );
    }

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        if (arguments.length <= 1) {
            sendUsageMessage(player);
            return;
        }

        val optionId = arguments[0].toLowerCase();
        val optionValue = arguments[1];
        if (!this.optionSetters.containsKey(optionId)) {
            sendUsageMessage(player);
            return;
        }

        val playerId = player.getUniqueId();
        val optionSetter = this.optionSetters.get(optionId);
        val status = this.teamOptionsManager.changeTeamOptions(
                playerId,
                teamOptions -> optionSetter.apply(teamOptions, player, optionValue)
        );

        if (status.equals(SUCCESSFUL)) {
            broadcastSuccess(playerId, optionId, optionValue);
            return;
        }

        val errorMessage = this.errorMessages.get(status);
        if (errorMessage != null) RavenMedia.sendMessage(player, errorMessage);
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public @NotNull Collection<String> suggest(@NotNull Player player, @NotNull String[] arguments) {
        val playerId = player.getUniqueId();
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) return Collections.emptyList();

        if (arguments.length == 1) {
            return playerTeam.isLeader(playerId) ?
                    suggestWithStartingPrefix(
                            List.of("tagName", "tagColor", "friendlyFire", "chatMuted", "setHome"),
                            arguments
                    ) :
                    Collections.emptyList();
        }

        if (arguments.length == 2) {
            return switch (arguments[0].toLowerCase()) {
                case "tagcolor" -> suggestWithStartingPrefix(TAG_COLORS, arguments);
                case "friendlyfire", "chatmuted" -> suggestWithStartingPrefix(List.of("true", "false"), arguments);
                case "sethome" -> suggestWithStartingPrefix(List.of("my-location", "remove"), arguments);
                default -> Collections.emptyList();
            };
        }

        return Collections.emptyList();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void broadcastSuccess(@NotNull UUID playerId,
                                  @NotNull String optionId, @NotNull String optionValue) {
        val playerTeam = this.teamManager.findTeamByPlayerId(playerId);
        if (playerTeam == null) return;

        val enabledText = RavenMedia.findValueByPath(playerId, MESSAGE_GENERAL_STATUS_ENABLED).asText();
        val disabledText = RavenMedia.findValueByPath(playerId, MESSAGE_GENERAL_STATUS_DISABLED).asText();

        val broadcastMessage = this.broadcastMessages.get(optionId);
        val teamOptions = (SMPTeamOptions) playerTeam.teamOptions();
        val placeholder = switch (optionId) {
            case "tagname" -> RavenPlaceholder.of("team_tag_name", optionValue);
            case "tagcolor" -> RavenPlaceholder.of(
                    "team_tag_color_name",
                    teamOptions.tagColorId() + teamOptions.tagColorDisplayName()
            );
            case "friendlyfire" -> RavenPlaceholder.of(
                    "team_friendly_fire_status",
                    teamOptions.friendlyFire() ? enabledText : disabledText
            );
            case "chatmuted" -> RavenPlaceholder.of(
                    "team_chat_muted_status",
                    teamOptions.chatMuted() ? disabledText : enabledText
            );
            default -> null;
        };

        if (broadcastMessage != null)
            if (placeholder != null)
                playerTeam.sendLocalizedMessage(
                        broadcastMessage,
                        RavenPlaceholderLike.builder()
                                .append(placeholder)
                                .build()
                );
            else
                playerTeam.sendLocalizedMessage(broadcastMessage);
    }

    private void sendUsageMessage(@NotNull Player player) {
        val separatorFormat = RavenMedia.findValueByPath(
                player.getUniqueId(),
                MESSAGE_COMMAND_TEAM_OPTIONS_USAGE_SEPARATOR_FORMAT
        ).asText();
        RavenMedia.sendMessage(
                player,
                MESSAGE_COMMAND_TEAM_OPTIONS_USAGE,
                RavenPlaceholderLike.builder()
                        .append(RavenPlaceholder.of(
                                "options",
                                String.join(separatorFormat, this.optionSetters.keySet())
                        ))
                        .build()
        );
    }

    @FunctionalInterface
    private interface TeamOptionSetter {
        void apply(@NotNull SMPTeamOptions options, @NotNull Player player, @NotNull String value);
    }

}