package ir.ariwuh.plugin.ravensmp.team;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamMember;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Accessors(fluent = true)
public final class SMPTeam implements RavenSMPTeam {

    @Getter
    private final @NotNull String teamId;
    @Getter
    @Setter
    private @NotNull RavenSMPTeamMember teamLeader;

    private final @NotNull HashSet<RavenSMPTeamMember> teamMembers;

    public SMPTeam(@NotNull String teamId, @NotNull SMPTeamMember teamLeader) {
        this.teamId = teamId;
        this.teamLeader = teamLeader;

        this.teamMembers = new HashSet<>();
        addMember(teamLeader);
    }

    @Override
    public void addMember(@NotNull RavenSMPTeamMember teamMember) {
        this.teamMembers.add(teamMember);
    }

    @Override
    public void removeMember(@NotNull UUID playerId) {
        this.teamMembers
                .stream()
                .filter(teamMember -> teamMember.playerId().equals(playerId))
                .findFirst()
                .ifPresent(this.teamMembers::remove);
    }

    @Override
    public boolean isMember(@NotNull UUID playerId) {
        return this.teamMembers
                .stream()
                .anyMatch(teamMember -> teamMember.playerId().equals(playerId));
    }

    @Override
    public boolean isLeader(@NotNull UUID playerId) {
        return this.teamLeader.playerId().equals(playerId);
    }

    @Override
    public @NotNull Collection<RavenSMPTeamMember> teamMembers() {
        return Set.copyOf(this.teamMembers);
    }

}