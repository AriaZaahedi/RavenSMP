package ir.ariwuh.plugin.ravensmp.command.subcommand.smp;

import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommand;
import ir.ariwuh.plugin.ravensmp.command.api.RavenSubCommandHandler;
import ir.ariwuh.plugin.ravensmp.manager.LanguageManager;
import ir.ariwuh.plugin.ravensmp.manager.PluginSettingsManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamTagManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
@RavenSubCommand(label = "reload", description = "Reload plugin config.")
public final class SMPReloadSubcommand extends RavenSubCommandHandler {

    private final @NotNull PluginSettingsManager pluginSettingsManager;
    private final @NotNull LanguageManager languageManager;
    private final @NotNull TeamTagManager teamTagManager;

    @Override
    public void execute(@NotNull Player player, @NonNull String[] arguments) {
        this.pluginSettingsManager.reloadPluginSettings();
        this.languageManager.reloadLanguages();
        this.teamTagManager.updateDefaultScoreboardTeamColor();
        player.sendRichMessage("<green>Plugin settings and languages have been reloaded successfully.");
    }

}