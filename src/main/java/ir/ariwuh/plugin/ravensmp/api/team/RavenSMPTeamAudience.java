package ir.ariwuh.plugin.ravensmp.api.team;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.RavenPlaceholder;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface RavenSMPTeamAudience extends ForwardingAudience {

    void sendLocalizedMessage(@NotNull RavenLanguagePath languagePath);

    void sendLocalizedMessage(@NotNull RavenLanguagePath languagePath,
                              @NotNull Collection<RavenPlaceholder> placeholders);

}