package ir.ariwuh.plugin.ravensmp.command.api;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Accessors(fluent = true)
public abstract class RavenCommand implements BasicCommand {

    private static final @NotNull RavenSMPPlugin PLUGIN = RavenSMPPlugin.instance();

    @Getter
    private final @NotNull String label;
    @Getter
    private final @Nullable String permission;

    public RavenCommand(@NotNull String label,
                        @Nullable String permission,
                        @NotNull Collection<String> aliases) {
        this.label = label;
        this.permission = permission;

        PLUGIN.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                commandEvent -> {
                    val registrar = commandEvent.registrar();
                    if (aliases.isEmpty()) registrar.register(label, this);
                    else registrar.register(label, aliases, this);
                }
        );
    }

    public RavenCommand(@NotNull String label, @Nullable String permission) {
        this(label, permission, List.of());
    }

    public RavenCommand(@NotNull String label, @NotNull Collection<String> aliases) {
        this(label, null, aliases);
    }

    public RavenCommand(@NotNull String label) {
        this(label, List.of());
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] arguments) {
        val commandSender = commandSourceStack.getExecutor();

        if (!(commandSender instanceof Player player)) return;

        if (this.permission != null && !player.hasPermission(this.permission)) {
            player.sendRichMessage("<red>You are not allowed to do this!");
            return;
        }

        try {
            execute(player, arguments);
        } catch (@NotNull Exception exception) {
            player.sendRichMessage(
                    "[ERROR] Failed to execute %s command. Please contact admin to solve this issue."
                            .formatted(this.label)
            );
            PLUGIN.getLogger().log(
                    Level.SEVERE,
                    "[ERROR] Failed to execute %s command. Please contact developer to solve this problem:"
                            .formatted(this.label),
                    exception
            );
        }
    }

    @Override
    public @NonNull Collection<String> suggest(@NonNull CommandSourceStack commandSourceStack, String @NonNull [] args) {
        if (!(commandSourceStack.getExecutor() instanceof Player player)) return Collections.emptyList();
        return suggest(player, args);
    }

    public abstract void execute(@NotNull Player player, @NotNull String[] arguments);

    public abstract @NotNull Collection<String> suggest(@NotNull Player player, @NotNull String[] arguments);

    public @NotNull Collection<String> suggestWithStartingPrefix(@NotNull List<String> strings,
                                                                 @NotNull String[] arguments) {
        if (arguments.length == 0) return strings;

        val lastArgument = arguments[arguments.length - 1].toLowerCase();
        return strings.stream()
                .filter(text -> text.startsWith(lastArgument))
                .collect(Collectors.toList());
    }

}