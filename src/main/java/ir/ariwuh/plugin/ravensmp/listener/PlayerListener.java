package ir.ariwuh.plugin.ravensmp.listener;

import ir.ariwuh.plugin.ravensmp.manager.AccountManager;
import ir.ariwuh.plugin.ravensmp.manager.LanguageManager;
import ir.ariwuh.plugin.ravensmp.manager.WorldManager;
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
    private final @NotNull WorldManager worldManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        event.joinMessage(null);
        val player = event.getPlayer();
        val playerId = player.getUniqueId();
        val playerName = player.getName();

        this.worldManager.broadcastJoinMessage(player);
        this.accountManager.loadAccount(playerId, playerName);
        this.teamManager.updateTeamAudience(playerId);
        this.teamManager.updateScoreboardTeamFor(playerId);
        this.teamManager.updateTeamMemberUsername(playerId, playerName);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        event.quitMessage(null);
        val player = event.getPlayer();
        val playerId = player.getUniqueId();

        this.worldManager.broadcastLeaveMessage(player);
        this.languageManager.unloadPlayerLanguage(playerId);
        this.accountManager.unloadAccount(playerId);
    }

}