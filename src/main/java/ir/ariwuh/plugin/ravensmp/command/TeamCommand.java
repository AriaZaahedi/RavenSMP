package ir.ariwuh.plugin.ravensmp.command;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.command.api.CommandParent;
import ir.ariwuh.plugin.ravensmp.command.subcommand.team.*;
import lombok.val;
import org.jetbrains.annotations.NotNull;

public final class TeamCommand extends CommandParent {

    public TeamCommand(@NotNull RavenSMPPlugin plugin) {
        super("team");

        val teamManager = plugin.teamManager();
        val teamInvitationManager = plugin.teamInvitationManager();
        val teamOptionsManager = plugin.teamOptionsManager();

        registerSubCommands(
                new TeamCreateSubcommand(teamManager),
                new TeamDisbandSubcommand(teamManager),
                new TeamKickMemberSubcommand(teamManager),
                new TeamLeaveSubcommand(teamManager),
                new TeamTransferSubcommand(teamManager),
                new TeamOptionsSubcommand(teamManager, teamOptionsManager),
                new TeamInviteSubcommand(teamManager, teamInvitationManager),
                new TeamAcceptInvitationSubcommand(teamInvitationManager),
                new TeamDeclineInvitationSubcommand(teamManager, teamInvitationManager)
        );
    }

}