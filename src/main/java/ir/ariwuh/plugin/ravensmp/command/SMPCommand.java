package ir.ariwuh.plugin.ravensmp.command;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.command.api.RavenCommandParent;
import ir.ariwuh.plugin.ravensmp.command.subcommand.smp.SMPReloadSubcommand;
import org.jetbrains.annotations.NotNull;

public final class SMPCommand extends RavenCommandParent {

    public SMPCommand(@NotNull RavenSMPPlugin plugin) {
        super("smp", "smp.command.smp");

        registerSubCommands(
                new SMPReloadSubcommand(
                        plugin.pluginSettingsManager(), plugin.languageManager(), plugin.teamTagManager()
                )
        );
    }

}