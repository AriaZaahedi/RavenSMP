package ir.ariwuh.plugin.ravensmp.api.team;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface RavenSMPTeamMember {

    @NotNull UUID playerId();

    @NotNull String username();

}