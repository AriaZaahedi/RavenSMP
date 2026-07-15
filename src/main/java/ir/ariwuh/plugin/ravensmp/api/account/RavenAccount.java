package ir.ariwuh.plugin.ravensmp.api.account;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface RavenAccount {

    @NotNull UUID accountId();

    @NotNull String username();

}