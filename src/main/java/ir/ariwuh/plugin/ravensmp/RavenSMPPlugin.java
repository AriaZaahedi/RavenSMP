package ir.ariwuh.plugin.ravensmp;

import ir.ariwuh.plugin.ravensmp.command.LanguageCommand;
import ir.ariwuh.plugin.ravensmp.command.SMPCommand;
import ir.ariwuh.plugin.ravensmp.command.TeamCommand;
import ir.ariwuh.plugin.ravensmp.listener.PlayerListener;
import ir.ariwuh.plugin.ravensmp.manager.LanguageManager;
import ir.ariwuh.plugin.ravensmp.manager.PluginSettingsManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamInvitationManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamOptionsManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamTagManager;
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
    private LanguageManager languageManager;

    private TeamTagManager teamTagManager;
    private TeamManager teamManager;
    private TeamInvitationManager teamInvitationManager;
    private TeamOptionsManager teamOptionsManager;

    @Override
    public void onEnable() {
        instance = this;

        this.pluginSettingsManager = new PluginSettingsManager(this);
        this.pluginSettingsManager.reloadPluginSettings();

        this.languageManager = new LanguageManager(this);
        this.languageManager.reloadLanguages();

        val pluginSettings = this.pluginSettingsManager.pluginSettings();

        this.teamTagManager = new TeamTagManager();
        this.teamManager = new TeamManager(pluginSettings, this.teamTagManager);
        this.teamInvitationManager = new TeamInvitationManager(
                this, pluginSettings, this.teamTagManager, this.teamManager
        );
        this.teamOptionsManager = new TeamOptionsManager(pluginSettings, this.teamTagManager, this.teamManager);

        getServer().getPluginManager().registerEvents(
                new PlayerListener(this.languageManager, this.teamManager),
                this
        );
        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;

        this.teamInvitationManager.removeActivePendingInvites();
        this.teamManager.unloadTeams();
        this.teamTagManager.unregisterScoreboardTeams();

        this.languageManager.unloadLanguages();
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

    private void registerCommands() {
        new SMPCommand(this);
        new LanguageCommand(this.languageManager);
        new TeamCommand(this);
    }

}