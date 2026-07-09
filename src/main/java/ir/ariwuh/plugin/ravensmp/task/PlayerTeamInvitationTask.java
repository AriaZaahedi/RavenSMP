package ir.ariwuh.plugin.ravensmp.task;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public final class PlayerTeamInvitationTask extends BukkitRunnable {

    private final @NotNull RavenSMPTeam team;
    private final @NotNull UUID targetId;

    private final @NotNull Consumer<PlayerTeamInvitationTask> whileRunning;

    private final @NotNull Runnable whenAccepted;
    private final @NotNull Runnable whenExpired;

    private int countdownTimer;

    public PlayerTeamInvitationTask(@NotNull PluginSettings pluginSettings,
                                    @NotNull RavenSMPTeam team, @NotNull UUID targetId,
                                    @NotNull Consumer<PlayerTeamInvitationTask> whileRunning,
                                    @NotNull Runnable whenAccepted, @NotNull Runnable whenExpired) {

        this.team = team;
        this.targetId = targetId;

        this.whileRunning = whileRunning;

        this.whenAccepted = whenAccepted;
        this.whenExpired = whenExpired;

        this.countdownTimer = pluginSettings.teamInviteExpirationTimeSeconds();
    }

    @Override
    public void run() {
        if (this.team.isMember(this.targetId)) {
            this.whenAccepted.run();
            cancel();
            return;
        }

        if (this.countdownTimer <= 0) {
            this.whenExpired.run();
            cancel();
            return;
        }

        this.whileRunning.accept(this);
        this.countdownTimer--;
    }

}