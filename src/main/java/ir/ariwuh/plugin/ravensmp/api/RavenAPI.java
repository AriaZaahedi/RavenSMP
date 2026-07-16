package ir.ariwuh.plugin.ravensmp.api;

import ir.ariwuh.plugin.ravensmp.api.adaptor.RavenAccountAdaptor;
import ir.ariwuh.plugin.ravensmp.api.adaptor.RavenLanguageAdaptor;
import ir.ariwuh.plugin.ravensmp.api.adaptor.RavenTeamAdaptor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
public abstract class RavenAPI {

    @Getter
    private static RavenAPI api;

    @ApiStatus.Internal
    public void instantiate() {
        api = this;
    }

    @ApiStatus.Internal
    public void unregister() {
        api = null;
    }

    @Contract(pure = true)
    public abstract @NotNull RavenLanguageAdaptor languageAdaptor();

    @Contract(pure = true)
    public abstract @NotNull RavenAccountAdaptor accountAdaptor();

    @Contract(pure = true)
    public abstract @NotNull RavenTeamAdaptor teamAdaptor();

}