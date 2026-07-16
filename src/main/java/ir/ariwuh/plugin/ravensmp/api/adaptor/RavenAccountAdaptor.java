package ir.ariwuh.plugin.ravensmp.api.adaptor;

import ir.ariwuh.plugin.ravensmp.api.account.RavenAccount;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface RavenAccountAdaptor {

    @Contract(pure = true)
    @Nullable RavenAccount findAccountById(@NotNull UUID accountId);

    @Contract(pure = true)
    @Nullable RavenAccount findOnlineAccountById(@NotNull UUID accountId);

    @Contract(pure = true)
    @NotNull Collection<RavenAccount> onlineAccounts();

}