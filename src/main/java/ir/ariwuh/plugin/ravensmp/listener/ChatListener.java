package ir.ariwuh.plugin.ravensmp.listener;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public final class ChatListener implements Listener, ChatRenderer {

    private final @NotNull PluginSettings pluginSettings;

    @EventHandler
    public void onChat(@NotNull AsyncChatEvent event) {
        event.renderer(this);
    }

    @Override
    public @NotNull Component render(@NonNull Player source,
                                     @NonNull Component sourceDisplayName,
                                     @NonNull Component message,
                                     @NonNull Audience viewer) {
        val globalChatFormat = this.pluginSettings.globalChatFormat();
        val displayNamePlaceholder = Placeholder.component("display_name", sourceDisplayName);
        val messagePlaceholder = Placeholder.component("message", message);

        return MiniMessage.miniMessage().deserialize(
                globalChatFormat, messagePlaceholder, displayNamePlaceholder
        );
    }

}