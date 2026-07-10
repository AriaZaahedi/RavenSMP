package ir.ariwuh.plugin.ravensmp.api.team;

import ir.ariwuh.plugin.ravensmp.api.language.LanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface RavenSMPTeam {

    void addMember(@NotNull RavenSMPTeamMember teamMember);

    void removeMember(@NotNull UUID playerId);

    void sendLocalizedMessage(@NotNull LanguagePath languagePath);

    void sendLocalizedMessage(@NotNull LanguagePath languagePath, @NotNull Collection<Placeholder> placeholders);

    boolean isMember(@NotNull UUID playerId);

    boolean isLeader(@NotNull UUID playerId);

    @NotNull String teamId();

    @NotNull RavenSMPTeamMember teamLeader();

    @NotNull Collection<RavenSMPTeamMember> teamMembers();

    @NotNull RavenSMPTeamAudience teamAudience();

}