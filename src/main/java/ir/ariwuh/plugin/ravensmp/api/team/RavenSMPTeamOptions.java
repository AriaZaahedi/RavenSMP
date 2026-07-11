package ir.ariwuh.plugin.ravensmp.api.team;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RavenSMPTeamOptions {

    @NotNull String tagName();

    @NotNull NamedTextColor tagColor();

    @Nullable Location homeLocation();

    boolean friendlyFire();

    boolean chatMuted();

}