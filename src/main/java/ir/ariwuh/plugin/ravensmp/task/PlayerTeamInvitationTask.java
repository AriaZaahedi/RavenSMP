package ir.ariwuh.plugin.ravensmp.task;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class PlayerTeamInvitationTask extends BukkitRunnable {

    private static final int TEAM_INVITE_EXPIRATION_TIME_SECONDS = 60;

    private final @NotNull RavenSMPTeam team;
    private final @NotNull UUID targetId;

    private final @NotNull Consumer<PlayerTeamInvitationTask> whileRunning;
    private final @NotNull Runnable whenAccepted;
    private final @NotNull Runnable whenExpired;

    private int countdownTimer = TEAM_INVITE_EXPIRATION_TIME_SECONDS;

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