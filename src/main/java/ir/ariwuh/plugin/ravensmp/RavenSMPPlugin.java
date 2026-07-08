package ir.ariwuh.plugin.ravensmp;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.plugin.java.JavaPlugin;

@Accessors(fluent = true)
public final class RavenSMPPlugin extends JavaPlugin {

    @Getter
    private static RavenSMPPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        instance = null;
    }

}