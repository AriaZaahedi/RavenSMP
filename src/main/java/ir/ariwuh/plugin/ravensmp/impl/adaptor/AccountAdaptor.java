package ir.ariwuh.plugin.ravensmp.impl.adaptor;

import ir.ariwuh.plugin.ravensmp.api.account.RavenAccount;
import ir.ariwuh.plugin.ravensmp.api.adaptor.RavenAccountAdaptor;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPAccountDao;
import ir.ariwuh.plugin.ravensmp.manager.AccountManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public final class AccountAdaptor implements RavenAccountAdaptor {

    private final @NotNull SMPAccountDao accountDao;
    private final @NotNull AccountManager accountManager;

    @Override
    public @Nullable RavenAccount findAccountById(@NotNull UUID accountId) {
        return this.accountDao.findById(accountId);
    }

    @Override
    public @Nullable RavenAccount findOnlineAccountById(@NotNull UUID accountId) {
        return this.accountManager.findOnlineById(accountId);
    }

    @Override
    public @NotNull Collection<RavenAccount> onlineAccounts() {
        return List.copyOf(this.accountManager.onlineAccounts());
    }

}