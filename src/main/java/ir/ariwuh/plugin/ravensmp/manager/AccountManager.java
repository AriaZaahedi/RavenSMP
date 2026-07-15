package ir.ariwuh.plugin.ravensmp.manager;

import ir.ariwuh.plugin.ravensmp.account.SMPAccount;
import ir.ariwuh.plugin.ravensmp.database.dao.SMPAccountDao;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class AccountManager {

    private final @NotNull LanguageManager languageManager;
    private final @NotNull SMPAccountDao accountDao;

    private final @NotNull ConcurrentHashMap<UUID, SMPAccount> onlineAccounts = new ConcurrentHashMap<>();

    public void loadAccount(@NotNull UUID playerId, @NotNull String username) {
        this.onlineAccounts.computeIfAbsent(playerId, _ -> {
            var account = this.accountDao.findById(playerId);
            if (account == null) {
                account = new SMPAccount(playerId, username);
                this.accountDao.insert(account);
            }

            // Update account's username if changed.
            if (!username.equals(account.username())) {
                account.username(username);
                this.accountDao.update(account);
            }

            // Update account's language if it does not exist.
            if (account.language() == null) {
                val defaultLanguage = this.languageManager.defaultLanguage();
                account.language(defaultLanguage);
            }

            this.languageManager.updatePlayerLanguage(playerId, account.language());

            return account;
        });
    }

    public void unloadAccount(@NotNull UUID playerId) {
        this.onlineAccounts.remove(playerId);
    }

    public void unloadAccounts() {
        this.onlineAccounts.clear();
    }

    @Contract(pure = true)
    public @Nullable SMPAccount findOnlineById(@NotNull UUID accountId) {
        return this.onlineAccounts.get(accountId);
    }

    @Contract(pure = true)
    public @NotNull Collection<SMPAccount> onlineAccounts() {
        return this.onlineAccounts.values();
    }

}