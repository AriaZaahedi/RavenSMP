package ir.ariwuh.plugin.ravensmp.api.event.team;

import ir.ariwuh.plugin.ravensmp.api.event.RavenEvent;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class RavenSMPTeamCreateEvent extends RavenEvent {

    private final @NotNull Player player;
    private final @NotNull RavenSMPTeam team;

}