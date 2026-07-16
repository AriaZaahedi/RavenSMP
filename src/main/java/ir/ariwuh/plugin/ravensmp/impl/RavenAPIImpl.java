package ir.ariwuh.plugin.ravensmp.impl;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import ir.ariwuh.plugin.ravensmp.api.RavenAPI;
import ir.ariwuh.plugin.ravensmp.impl.adaptor.AccountAdaptor;
import ir.ariwuh.plugin.ravensmp.impl.adaptor.LanguageAdaptor;
import ir.ariwuh.plugin.ravensmp.impl.adaptor.TeamAdaptor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class RavenAPIImpl extends RavenAPI {

    private final @NotNull RavenSMPPlugin plugin;

    @Getter
    private LanguageAdaptor languageAdaptor;
    @Getter
    private AccountAdaptor accountAdaptor;
    @Getter
    private TeamAdaptor teamAdaptor;

    @Override
    public void instantiate() {
        super.instantiate();

        this.accountAdaptor = new AccountAdaptor(this.plugin.accountDao(), this.plugin.accountManager());
        this.languageAdaptor = new LanguageAdaptor(this.plugin.languageManager());
        this.teamAdaptor = new TeamAdaptor(this.plugin.teamManager());
    }

}