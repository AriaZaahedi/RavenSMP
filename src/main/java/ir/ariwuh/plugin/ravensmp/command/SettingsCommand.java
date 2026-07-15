package ir.ariwuh.plugin.ravensmp.command;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.command.api.RavenCommandParent;
import ir.ariwuh.plugin.ravensmp.command.subcommand.settings.SettingsToggleTeamInvitesSubcommand;
import org.jetbrains.annotations.NotNull;

public final class SettingsCommand extends RavenCommandParent {

    public SettingsCommand(@NotNull RavenSMPPlugin plugin) {
        super("settings");

        registerSubCommands(
                new SettingsToggleTeamInvitesSubcommand(
                        plugin.accountDao(), plugin.accountManager()
                )
        );
    }

}