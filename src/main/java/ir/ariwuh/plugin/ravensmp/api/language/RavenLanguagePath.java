package ir.ariwuh.plugin.ravensmp.api.language;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@RequiredArgsConstructor
@Getter
public enum RavenLanguagePath {

    // === GENERAL ===
    // > Status
    MESSAGE_GENERAL_STATUS_ENABLED("message.general.status.enabled"),
    MESSAGE_GENERAL_STATUS_DISABLED("message.general.status.disabled"),

    // === COMMANDS ===
    // > General
    MESSAGE_COMMAND_GENERAL_ERROR_NO_PERMISSION("message.command.general.error.no-permission"),
    MESSAGE_COMMAND_GENERAL_ERROR_EXCEPTION("message.command.general.error.exception"),
    MESSAGE_COMMAND_GENERAL_ERROR_PLAYER_OFFLINE("message.command.general.error.player-not-found"),
    MESSAGE_COMMAND_GENERAL_ERROR_ACCOUNT_NOT_FOUND("message.command.general.error.account-not-found"),

    // > Language
    MESSAGE_COMMAND_LANGUAGE_USAGE("message.command.language.usage"),
    MESSAGE_COMMAND_LANGUAGE_USAGE_SEPARATOR_FORMAT("message.command.language.usage-separator-format"),
    MESSAGE_COMMAND_LANGUAGE_PLAYER_UPDATED("message.command.language.updated"),
    MESSAGE_COMMAND_LANGUAGE_ERROR_ALREADY_SELECTED("message.command.language.error.already-selected"),
    MESSAGE_COMMAND_LANGUAGE_ERROR_NOT_FOUND("message.command.language.error.not-found"),

    // > Settings
    MESSAGE_COMMAND_SETTINGS_TEAM_INVITES_TOGGLED("message.command.player-settings.team.invites-toggled"),

    // > Team

    // --- General ---
    // Id
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_NOT_EXISTS("message.command.team.general.error.id.not-exists"),
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_LONG("message.command.team.general.error.id.long"),
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_ID_INVALID("message.command.team.general.error.id.invalid"),

    // Tags
    MESSAGE_COMMAND_TEAM_GENERAL_TAG_ERROR_INVALID_NAME("message.command.team.general.error.tag.invalid"),
    MESSAGE_COMMAND_TEAM_GENERAL_TAG_ERROR_INVALID_COLOR("message.command.team.general.error.tag.invalid-color"),
    MESSAGE_COMMAND_TEAM_GENERAL_TAG_ERROR_LONG("message.command.team.general.error.tag.long"),

    // Player
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_HAS_TEAM("message.command.team.general.error.player.has-team"),
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_LACKING_TEAM("message.command.team.general.error.player.lacking-team"),
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_PLAYER_NOT_LEADER("message.command.team.general.error.player.not-leader"),

    // Target
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_SELF("message.command.team.general.error.target.is-self"),
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_IN_TEAM("message.command.team.general.error.target.is-in-team"),
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_IS_IN_ANOTHER_TEAM("message.command.team.general.error.target.is-in-another-team"),
    MESSAGE_COMMAND_TEAM_GENERAL_ERROR_TARGET_LACKING_TEAM("message.command.team.general.error.target.lacking-team"),

    // --- Creation ---
    MESSAGE_COMMAND_TEAM_CREATION_USAGE("message.command.team.creation.usage"),
    MESSAGE_COMMAND_TEAM_CREATION_SUCCESS("message.command.team.creation.success"),
    MESSAGE_COMMAND_TEAM_CREATION_ERROR_EXISTS("message.command.team.creation.error.exists"),
    MESSAGE_COMMAND_TEAM_CREATION_ERROR_ON_COOLDOWN("message.command.team.creation.error.on-cooldown"),

    // --- Disband ---
    MESSAGE_COMMAND_TEAM_DISBAND_CONFIRMATION("message.command.team.disband.confirmation"),
    MESSAGE_COMMAND_TEAM_DISBAND_ERROR_INVALID_TEAM_ID("message.command.team.disband.error.invalid-team-id"),

    // --- Invitation ---
    MESSAGE_COMMAND_TEAM_INVITATION_DECLINED("message.command.team.invitation.declined"),
    MESSAGE_COMMAND_TEAM_INVITATION_USAGE_INVITE("message.command.team.invitation.usage.invite"),
    MESSAGE_COMMAND_TEAM_INVITATION_USAGE_ACCEPT("message.command.team.invitation.usage.accept"),
    MESSAGE_COMMAND_TEAM_INVITATION_USAGE_DECLINE("message.command.team.invitation.usage.decline"),
    MESSAGE_COMMAND_TEAM_INVITATION_ERROR_ALREADY_INVITED("message.command.team.invitation.error.already-invited"),
    MESSAGE_COMMAND_TEAM_INVITATION_ERROR_LACKING("message.command.team.invitation.error.lacking"),
    MESSAGE_COMMAND_TEAM_INVITATION_ERROR_ON_CAPACITY("message.command.team.invitation.error.on-capacity"),
    MESSAGE_COMMAND_TEAM_INVITATION_ERROR_TARGET_INVITE_DECLINED("message.command.team.invitation.error.target.invite-declined"),
    MESSAGE_COMMAND_TEAM_INVITATION_ERROR_TARGET_INVITE_DISABLED("message.command.team.invitation.error.target.invite-disabled"),

    // --- Leave ---
    MESSAGE_COMMAND_TEAM_LEAVE_SUCCESS("message.command.team.leave.success"),
    MESSAGE_COMMAND_TEAM_LEAVE_ERROR_IS_LEADER("message.command.team.leave.error.is-leader"),

    // --- Kick ---
    MESSAGE_COMMAND_TEAM_KICK_USAGE("message.command.team.kick.usage"),
    MESSAGE_COMMAND_TEAM_KICK_TARGET("message.command.team.kick.target"),

    // --- Transfer ---
    MESSAGE_COMMAND_TEAM_TRANSFER_USAGE("message.command.team.transfer.usage"),

    // --- Home ---
    MESSAGE_COMMAND_TEAM_HOME_ERROR_NOT_EXISTS("message.command.team.home.error.not-exists"),
    MESSAGE_COMMAND_TEAM_HOME_ERROR_ON_COOLDOWN("message.command.team.home.error.on-cooldown"),

    // --- Chat ---
    MESSAGE_COMMAND_TEAM_CHAT_ERROR_MUTED("message.command.team.chat.error.muted"),
    MESSAGE_COMMAND_TEAM_CHAT_FORMAT("message.command.team.chat.format"),

    // -- Option ---
    // Usage
    MESSAGE_COMMAND_TEAM_OPTIONS_USAGE("message.command.team.option.usage"),
    MESSAGE_COMMAND_TEAM_OPTIONS_USAGE_SEPARATOR_FORMAT("message.command.team.option.usage-separator-format"),

    MESSAGE_COMMAND_TEAM_OPTIONS_ERROR_HOME_BLACKLISTED_WORLD("message.command.team.option.error.home.blacklisted-world"),
    MESSAGE_COMMAND_TEAM_OPTIONS_ERROR_UNKNOWN("message.command.team.option.error.unknown"),

    // === BROADCASTING ===
    // Server
    BROADCAST_SERVER_GENERAL_PREFIX("broadcast.server.general.prefix"),
    BROADCAST_SERVER_GENERAL_PLAYER_JOIN("broadcast.server.general.player-join"),
    BROADCAST_SERVER_GENERAL_PLAYER_LEAVE("broadcast.server.general.player-leave"),

    // Team
    BROADCAST_TEAM_GENERAL_PREFIX("broadcast.team.general.prefix"),
    BROADCAST_TEAM_GENERAL_MEMBER_JOIN("broadcast.team.general.member-join"),
    BROADCAST_TEAM_GENERAL_MEMBER_LEAVE("broadcast.team.general.member-leave"),
    BROADCAST_TEAM_GENERAL_PLAYER_JOIN("broadcast.team.general.player-join"),
    BROADCAST_TEAM_GENERAL_PLAYER_LEAVE("broadcast.team.general.player-leave"),

    BROADCAST_TEAM_DISBAND("broadcast.team.disband"),
    BROADCAST_TEAM_KICK("broadcast.team.kick"),
    BROADCAST_TEAM_TRANSFER("broadcast.team.transfer"),

    BROADCAST_TEAM_INVITATION_MEMBERS("broadcast.team.invitation.members"),
    BROADCAST_TEAM_INVITATION_TARGET("broadcast.team.invitation.target"),
    BROADCAST_TEAM_INVITATION_EXPIRED("broadcast.team.invitation.expired"),
    BROADCAST_TEAM_INVITATION_EXPIRED_TARGET("broadcast.team.invitation.expired-target"),

    BROADCAST_TEAM_OPTION_TAG_NAME_CHANGED("broadcast.team.option.tag-name-changed"),
    BROADCAST_TEAM_OPTION_TAG_COLOR_CHANGED("broadcast.team.option.tag-color-changed"),

    BROADCAST_TEAM_OPTION_FRIENDLY_FIRE_TOGGLED("broadcast.team.option.friendly-fire-toggled"),
    BROADCAST_TEAM_OPTION_CHAT_TOGGLED("broadcast.team.option.chat-toggled"),
    BROADCAST_TEAM_OPTION_HOME_UPDATED("broadcast.team.option.home-updated");

    private final @NotNull String languagePath;

}