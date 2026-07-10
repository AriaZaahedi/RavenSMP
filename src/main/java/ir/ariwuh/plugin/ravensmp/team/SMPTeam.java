package ir.ariwuh.plugin.ravensmp.team;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamMember;
import ir.ariwuh.plugin.ravensmp.api.language.LanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.Placeholder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Accessors(fluent = true)
public final class SMPTeam implements RavenSMPTeam {

    @Getter
    private final @NotNull String teamId;
    @Getter
    @Setter
    private @NotNull RavenSMPTeamMember teamLeader;

    private final @NotNull HashSet<RavenSMPTeamMember> teamMembers;

    @Getter
    private SMPTeamAudience teamAudience;

    public SMPTeam(@NotNull String teamId, @NotNull SMPTeamMember teamLeader) {
        this.teamId = teamId;
        this.teamLeader = teamLeader;

        this.teamMembers = new HashSet<>();
        addMember(teamLeader);
    }

    @Override
    public void addMember(@NotNull RavenSMPTeamMember teamMember) {
        this.teamMembers.add(teamMember);
        updateTeamAudience();
    }

    @Override
    public void removeMember(@NotNull UUID playerId) {
        this.teamMembers
                .stream()
                .filter(teamMember -> teamMember.playerId().equals(playerId))
                .findFirst()
                .ifPresent(teamMember -> {
                    this.teamMembers.remove(teamMember);
                    updateTeamAudience();
                });
    }

    @Override
    public void sendLocalizedMessage(@NotNull LanguagePath languagePath) {
        if (this.teamAudience == null) return;
        this.teamAudience.sendLocalizedMessage(languagePath);
    }

    @Override
    public void sendLocalizedMessage(@NotNull LanguagePath languagePath,
                                     @NotNull Collection<Placeholder> placeholders) {
        if (this.teamAudience == null) return;
        this.teamAudience.sendLocalizedMessage(languagePath, placeholders);
    }

    public void updateTeamAudience() {
        this.teamAudience = SMPTeamAudience.of(
                this.teamMembers.stream()
                        .map(RavenSMPTeamMember::playerId)
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .toList()
        );
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