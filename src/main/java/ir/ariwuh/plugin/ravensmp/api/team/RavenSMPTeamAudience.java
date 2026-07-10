package ir.ariwuh.plugin.ravensmp.api.team;

import ir.ariwuh.plugin.ravensmp.api.language.LanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.Placeholder;
import net.kyori.adventure.audience.ForwardingAudience;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface RavenSMPTeamAudience extends ForwardingAudience {

    void sendLocalizedMessage(@NotNull LanguagePath languagePath);

    void sendLocalizedMessage(@NotNull LanguagePath languagePath,
                              @NotNull Collection<Placeholder> placeholders);

}