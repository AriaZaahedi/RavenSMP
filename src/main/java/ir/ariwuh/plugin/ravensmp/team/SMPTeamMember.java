package ir.ariwuh.plugin.ravensmp.team;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamMember;
import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Accessors(fluent = true)
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(of = "playerId")
public final class SMPTeamMember implements RavenSMPTeamMember {

    private @NotNull UUID playerId;
    private @NotNull String username;

}