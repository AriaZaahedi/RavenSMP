package ir.ariwuh.plugin.ravensmp.impl.adaptor;

import ir.ariwuh.plugin.ravensmp.api.adaptor.RavenTeamAdaptor;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor
public final class TeamAdaptor implements RavenTeamAdaptor {

    private final @NotNull TeamManager teamManager;

    @Override
    public @Nullable RavenSMPTeam findTeamById(@NotNull String teamId) {
        return this.teamManager.findTeamById(teamId);
    }

    @Override
    public @Nullable RavenSMPTeam findTeamByPlayerId(@NotNull UUID playerId) {
        return this.teamManager.findTeamByPlayerId(playerId);
    }

}