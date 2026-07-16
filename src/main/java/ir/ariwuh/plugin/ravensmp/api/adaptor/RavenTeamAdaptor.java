package ir.ariwuh.plugin.ravensmp.api.adaptor;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface RavenTeamAdaptor {

    @Contract(pure = true)
    @Nullable RavenSMPTeam findTeamById(@NotNull String teamId);

    @Contract(pure = true)
    @Nullable RavenSMPTeam findTeamByPlayerId(@NotNull UUID playerId);

}