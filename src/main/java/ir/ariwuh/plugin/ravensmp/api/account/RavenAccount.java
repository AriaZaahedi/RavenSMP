package ir.ariwuh.plugin.ravensmp.api.account;

import ir.ariwuh.plugin.ravensmp.api.language.RavenLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface RavenAccount {

    @NotNull UUID accountId();

    @NotNull String username();

    @NotNull RavenLanguage language();

    @NotNull RavenAccountSettings accountSettings();

}