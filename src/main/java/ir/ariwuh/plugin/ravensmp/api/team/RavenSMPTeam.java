package ir.ariwuh.plugin.ravensmp.api.team;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface RavenSMPTeam {

    void addMember(@NotNull RavenSMPTeamMember teamMember);

    void removeMember(@NotNull UUID playerId);

    boolean isMember(@NotNull UUID playerId);

    boolean isLeader(@NotNull UUID playerId);

    @NotNull String teamId();

    @NotNull RavenSMPTeamMember teamLeader();

    @NotNull Collection<RavenSMPTeamMember> teamMembers();

}