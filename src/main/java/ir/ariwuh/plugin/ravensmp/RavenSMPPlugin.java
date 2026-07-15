package ir.ariwuh.plugin.ravensmp;

import ir.ariwuh.plugin.ravensmp.command.LanguageCommand;
import ir.ariwuh.plugin.ravensmp.command.SMPCommand;
import ir.ariwuh.plugin.ravensmp.command.SettingsCommand;
import ir.ariwuh.plugin.ravensmp.command.TeamCommand;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPAccountDao;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPTeamDao;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPTeamOptionsDao;
import ir.ariwuh.plugin.ravensmp.listener.ChatListener;
import ir.ariwuh.plugin.ravensmp.listener.PlayerListener;
import ir.ariwuh.plugin.ravensmp.manager.*;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamInvitationManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamOptionsManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamTagManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Accessors(fluent = true)
@Getter
public final class RavenSMPPlugin extends JavaPlugin {

    @Getter
    private static RavenSMPPlugin instance;

    private PluginSettingsManager pluginSettingsManager;
    private LanguageManager languageManager;

    private DatabaseManager databaseManager;

    private SMPTeamDao teamDao;
    private SMPTeamOptionsDao teamOptionsDao;

    private SMPAccountDao accountDao;
    private AccountManager accountManager;

    private TeamTagManager teamTagManager;
    private TeamManager teamManager;
    private TeamInvitationManager teamInvitationManager;
    private TeamOptionsManager teamOptionsManager;

    private WorldManager worldManager;

    @Override
    public void onEnable() {
        instance = this;

        this.pluginSettingsManager = new PluginSettingsManager(this);
        this.pluginSettingsManager.reloadPluginSettings();

        this.languageManager = new LanguageManager(this);
        this.languageManager.reloadLanguages();

        val pluginSettings = this.pluginSettingsManager.pluginSettings();

        this.databaseManager = new DatabaseManager(pluginSettings);

        this.teamDao = new SMPTeamDao(this.databaseManager);
        this.teamOptionsDao = new SMPTeamOptionsDao(this.databaseManager);

        this.accountDao = new SMPAccountDao(this.databaseManager, this.languageManager);
        this.accountManager = new AccountManager(this.languageManager, this.accountDao);

        this.teamTagManager = new TeamTagManager(pluginSettings);
        this.teamManager = new TeamManager(pluginSettings, this.teamDao, this.teamTagManager);
        this.teamManager.loadTeams();
        this.teamInvitationManager = new TeamInvitationManager(
                this, pluginSettings, this.teamDao, this.accountManager, this.teamTagManager, this.teamManager
        );
        this.teamOptionsManager = new TeamOptionsManager(
                pluginSettings, this.teamOptionsDao, this.teamTagManager, this.teamManager
        );

        this.worldManager = new WorldManager(this, this.teamManager);

        registerListeners(
                new ChatListener(pluginSettings),
                new PlayerListener(this.languageManager, this.accountManager, this.teamManager, this.worldManager)
        );
        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;

        this.teamInvitationManager.removeActivePendingInvites();
        this.teamManager.unloadTeams();
        this.teamTagManager.unregisterScoreboardTeams();

        this.accountManager.unloadAccounts();

        this.databaseManager.close();

        this.languageManager.unloadLanguages();
    }

    public void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

    private void registerListeners(@NotNull Listener... listeners) {
        val pluginManager = getServer().getPluginManager();
        Arrays.stream(listeners)
                .forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    private void registerCommands() {
        new LanguageCommand(this.languageManager, this.accountDao, this.accountManager);
        new SettingsCommand(this);
        new SMPCommand(this);
        new TeamCommand(this);
    }

}