package ir.ariwuh.plugin.ravensmp.team;

import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeam;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamOptions;
import ir.ariwuh.plugin.ravensmp.utility.StringUtility;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

@Accessors(fluent = true)
@Getter
@Setter
@EqualsAndHashCode
@ToString(of = {"tagName", "tagColor", "homeLocation", "friendlyFire", "chatMuted"})
public final class SMPTeamOptions implements RavenSMPTeamOptions {

    private final @NotNull RavenSMPTeam smpTeam;

    private @NotNull String tagName;
    private @UnknownNullability NamedTextColor tagColor = NamedTextColor.AQUA;

    private @Nullable Location homeLocation;

    private boolean friendlyFire;
    private boolean chatMuted;

    public SMPTeamOptions(@NotNull RavenSMPTeam smpTeam) {
        this.smpTeam = smpTeam;
        this.tagName = smpTeam.teamId();
    }

    public @NotNull String tagColorId() {
        return "<" + this.tagColor + ">";
    }

    public @NotNull String tagColorDisplayName() {
        return StringUtility.formatTagColorName(this.tagColor.toString());
    }

    @FunctionalInterface
    public interface Builder {

        void apply(@NotNull SMPTeamOptions teamOptions);

    }

}