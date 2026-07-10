package ir.ariwuh.plugin.ravensmp.team;

import ir.ariwuh.plugin.ravensmp.api.language.LanguagePath;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.Placeholder;
import ir.ariwuh.plugin.ravensmp.api.language.placeholder.PlaceholderLike;
import ir.ariwuh.plugin.ravensmp.api.team.RavenSMPTeamAudience;
import ir.ariwuh.plugin.ravensmp.utility.RavenMedia;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SMPTeamAudience implements RavenSMPTeamAudience {

    private final @NotNull Collection<? extends Player> players;

    public static @NotNull SMPTeamAudience of(@NotNull Collection<? extends Player> players) {
        return new SMPTeamAudience(players);
    }

    @Override
    public void sendLocalizedMessage(@NotNull LanguagePath languagePath) {
        this.players.forEach(player -> {
            val prefix = RavenMedia.findValueByPath(player.getUniqueId(), LanguagePath.BROADCAST_TEAM_GENERAL_PREFIX).asText();
            RavenMedia.sendMessage(
                    player,
                    languagePath,
                    PlaceholderLike.builder()
                            .append("team_broadcast_prefix", prefix)
                            .build()
            );
        });
    }

    @Override
    public void sendLocalizedMessage(@NotNull LanguagePath languagePath,
                                     @NotNull Collection<Placeholder> placeholders) {
        this.players.forEach(player -> {
            val prefix = RavenMedia.findValueByPath(player.getUniqueId(), LanguagePath.BROADCAST_TEAM_GENERAL_PREFIX).asText();
            RavenMedia.sendMessage(
                    player,
                    languagePath,
                    PlaceholderLike.builder()
                            .append("team_broadcast_prefix", prefix)
                            .append(placeholders)
                            .build()
            );
        });
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return this.players;
    }

}