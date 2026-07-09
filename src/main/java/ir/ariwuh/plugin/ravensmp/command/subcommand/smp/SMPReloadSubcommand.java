package ir.ariwuh.plugin.ravensmp.command.subcommand.smp;

import ir.ariwuh.plugin.ravensmp.command.api.SubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.SubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.PluginSettingsManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@SubCommand(label = "reload", description = "Reload the config.")
public final class SMPReloadSubcommand extends SubCommandHandler {

    private final @NotNull PluginSettingsManager pluginSettingsManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        this.pluginSettingsManager.reloadPluginSettings();
        player.sendRichMessage("<green>Plugin settings have been reloaded successfully.");
    }

}