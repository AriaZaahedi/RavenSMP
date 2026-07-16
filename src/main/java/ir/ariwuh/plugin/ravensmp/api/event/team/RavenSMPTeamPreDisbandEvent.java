package ir.ariwuh.plugin.ravensmp.api.event.team;

import ir.ariwuh.plugin.ravensmp.api.event.RavenCancellableEvent;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamMember;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class RavenSMPTeamPreDisbandEvent extends RavenCancellableEvent {

    private final @NotNull RavenSMPTeamMember whoDisbanded;
    private final @NotNull RavenSMPTeam team;

}