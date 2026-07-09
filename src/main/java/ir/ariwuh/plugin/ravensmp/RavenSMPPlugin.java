package ir.ariwuh.plugin.ravensmp;

import ir.ariwuh.plugin.ravensmp.command.TeamCommand;
import ir.ariwuh.plugin.ravensmp.manager.TeamInvitationManager;
import ir.ariwuh.plugin.ravensmp.manager.TeamManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
@Getter
public final class RavenSMPPlugin extends JavaPlugin {

    @Getter
    private static RavenSMPPlugin instance;

    private TeamManager teamManager;
    private TeamInvitationManager teamInvitationManager;

    @Override
    public void onEnable() {
        instance = this;

        this.teamManager = new TeamManager();
        this.teamInvitationManager = new TeamInvitationManager(this, this.teamManager);

        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;

        this.teamInvitationManager.removeActivePendingInvites();
        this.teamManager.unloadTeams();
    }

    private void registerCommands() {
        new TeamCommand(this);
    }

}