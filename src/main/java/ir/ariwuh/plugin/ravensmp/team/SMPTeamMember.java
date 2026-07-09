package ir.ariwuh.plugin.ravensmp.team;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamMember;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Accessors(fluent = true)
@Data
public final class SMPTeamMember implements RavenSMPTeamMember {

    private final @NotNull UUID playerId;
    private @NotNull String username;

}