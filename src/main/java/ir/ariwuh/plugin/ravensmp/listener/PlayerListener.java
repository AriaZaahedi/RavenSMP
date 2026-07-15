package ir.ariwuh.plugin.ravensmp.listener;

import ir.ariwuh.plugin.ravensmp.manager.AccountManager;
import ir.ariwuh.plugin.ravensmp.manager.LanguageManager;
import ir.ariwuh.plugin.ravensmp.manager.team.TeamManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class PlayerListener implements Listener {

    private final @NotNull LanguageManager languageManager;
    private final @NotNull AccountManager accountManager;
    private final @NotNull TeamManager teamManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        val playerId = event.getPlayer().getUniqueId();
        val playerName = event.getPlayer().getName();

        this.accountManager.loadAccount(playerId, playerName);
        this.teamManager.updateTeamAudience(playerId);
        this.teamManager.updateScoreboardTeamFor(playerId);
        this.teamManager.updateTeamMemberUsername(playerId, playerName);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        val playerId = event.getPlayer().getUniqueId();
        this.languageManager.unloadPlayerLanguage(playerId);
        this.accountManager.unloadAccount(playerId);
    }

}