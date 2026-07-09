package ir.ariwuh.plugin.ravensmp;

import ir.ariwuh.plugin.ravensmp.command.TeamCommand;
import ir.ariwuh.plugin.ravensmp.manager.PluginSettingsManager;
import ir.ariwuh.plugin.ravensmp.manager.TeamInvitationManager;
import ir.ariwuh.plugin.ravensmp.manager.TeamManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
@Getter
public final class RavenSMPPlugin extends JavaPlugin {

    @Getter
    private static RavenSMPPlugin instance;

    private PluginSettingsManager pluginSettingsManager;

    private TeamManager teamManager;
    private TeamInvitationManager teamInvitationManager;

    @Override
    public void onEnable() {
        instance = this;

        this.pluginSettingsManager = new PluginSettingsManager(this);
        this.pluginSettingsManager.reloadPluginSettings();

        val pluginSettings = this.pluginSettingsManager.pluginSettings();

        this.teamManager = new TeamManager(pluginSettings);
        this.teamInvitationManager = new TeamInvitationManager(this, pluginSettings, this.teamManager);

        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;

        this.teamInvitationManager.removeActivePendingInvites();
        this.teamManager.unloadTeams();
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

    private void registerCommands() {
        new TeamCommand(this);
    }

}