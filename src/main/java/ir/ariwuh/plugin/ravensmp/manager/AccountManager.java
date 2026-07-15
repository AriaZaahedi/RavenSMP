package ir.ariwuh.plugin.ravensmp.manager;

import ir.ariwuh.plugin.ravensmp.account.SMPAccount;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPAccountDao;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class AccountManager {

    private final @NotNull SMPAccountDao accountDao;
    private final @NotNull ConcurrentHashMap<UUID, SMPAccount> onlineAccounts = new ConcurrentHashMap<>();

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull SMPAccount loadAccount(@NotNull UUID playerId, @NotNull String username) {
        return this.onlineAccounts.computeIfAbsent(playerId, _ -> {
            var account = this.accountDao.findById(playerId);
            if (account == null) {
                account = new SMPAccount(playerId, username);
                this.accountDao.insert(account);
            } else if (!username.equals(account.username())) {
                account.username(username);
                this.accountDao.update(account);
            }

            return account;
        });
    }

    public void unloadAccount(@NotNull UUID playerId) {
        this.onlineAccounts.remove(playerId);
    }

    public void unloadAccounts() {
        this.onlineAccounts.clear();
    }

    public @Nullable SMPAccount findOnlineById(@NotNull UUID accountId) {
        return this.onlineAccounts.get(accountId);
    }

    public @NotNull Collection<SMPAccount> onlineAccounts() {
        return this.onlineAccounts.values();
    }

}