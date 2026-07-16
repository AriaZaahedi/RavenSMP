package ir.ariwuh.plugin.ravensmp.utility;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.api.event.RavenEvent;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class RavenUtility {

    public void runAsync(@NotNull Runnable action) {
        Bukkit.getScheduler().runTaskAsynchronously(RavenSMPPlugin.instance(), action);
    }

    public void callEvent(@NotNull RavenEvent event) {
        Bukkit.getPluginManager().callEvent(event);
    }

}