package ir.ariwuh.plugin.ravensmp.api.team;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public interface RavenSMPTeam {

    void addMember(@NotNull RavenSMPTeamMember teamMember);

    void removeMember(@NotNull UUID playerId);

    void sendLocalizedMessage(@NotNull RavenLanguagePath languagePath);

    void sendLocalizedMessage(@NotNull RavenLanguagePath languagePath, @NotNull Collection<RavenPlaceholder> placeholders);

    boolean isMember(@NotNull UUID playerId);

    boolean isLeader(@NotNull UUID playerId);

    @NotNull String teamId();

    @NotNull RavenSMPTeamMember teamLeader();

    @NotNull Collection<RavenSMPTeamMember> teamMembers();

    @NotNull RavenSMPTeamOptions teamOptions();

    @NotNull RavenSMPTeamAudience teamAudience();

}