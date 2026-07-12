package ir.ariwuh.plugin.ravensmp.command.api;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class RavenCommandParent extends RavenCommand {

    private final @NotNull List<RavenSubCommandHandler> subCommands;
    private final @NotNull List<String> subCommandsLabels;
    private final @NotNull String version;
    private @Nullable String helpMessage;

    public RavenCommandParent(@NotNull String label,
                              @Nullable String permission,
                              @NotNull Collection<String> aliases) {
        super(label, permission, aliases);
        this.subCommands = new ArrayList<>();
        this.subCommandsLabels = new ArrayList<>();
        this.version = RavenSMPPlugin.instance().getPluginMeta().getVersion();
    }

    public RavenCommandParent(@NotNull String label,
                              @Nullable String permission) {
        this(label, permission, List.of());
    }

    public RavenCommandParent(@NotNull String label,
                              @NotNull Collection<String> aliases) {
        this(label, null, aliases);
    }

    public RavenCommandParent(@NotNull String label) {
        this(label, null, List.of());
    }

    private void registerSubCommand(@NotNull RavenSubCommandHandler subCommand) {
        this.subCommands.add(subCommand);
        this.subCommandsLabels.add(subCommand.label().toLowerCase());
    }

    public void registerSubCommands(@NotNull RavenSubCommandHandler... subCommandsToRegister) {
        Arrays.stream(subCommandsToRegister).forEach(this::registerSubCommand);
        this.helpMessage = buildHelpMessage(label(), this.version, this.subCommands);
    }

    private @NotNull String helpMessage() {
        if (this.helpMessage == null) this.helpMessage = buildHelpMessage(label(), this.version, this.subCommands);
        return this.helpMessage;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] arguments) {
        executeSubCommand(this.subCommands, player, arguments, helpMessage());
    }

    @Override
    public @NonNull Collection<String> suggest(@NotNull Player player, @NotNull String[] arguments) {
        return suggestSubCommand(this.subCommands, this.subCommandsLabels, player, arguments);
    }

    private @Nullable RavenSubCommandHandler findSubCommand(@NotNull List<RavenSubCommandHandler> subCommands,
                                                            @NotNull String label) {
        return subCommands.stream()
                .filter(subExecutor -> subExecutor.label().equalsIgnoreCase(label))
                .findFirst()
                .orElse(null);
    }

    public void executeSubCommand(@NotNull List<RavenSubCommandHandler> subCommands,
                                  @NotNull Player player,
                                  @NotNull String[] arguments,
                                  @Nullable String helpMessage) {
        if (arguments.length == 0) {
            if (helpMessage != null) player.sendRichMessage(helpMessage);
            return;
        }

        val subExecutor = findSubCommand(subCommands, arguments[0]);
        if (subExecutor != null) {
            subExecutor.execute(
                    player,
                    java.util.Arrays.stream(arguments)
                            .skip(1)
                            .toArray(String[]::new)
            );
            return;
        }

        if (helpMessage != null) player.sendRichMessage(helpMessage);
    }

    public @NotNull Collection<String> suggestSubCommand(@NotNull List<RavenSubCommandHandler> subCommands,
                                                         @NotNull List<String> subCommandLabels,
                                                         @NotNull Player player,
                                                         @NotNull String[] arguments) {
        return switch (arguments.length) {
            case 0 -> subCommandLabels;
            case 1 -> suggestWithStartingPrefix(subCommandLabels, arguments);
            default -> {
                val subExecutor = findSubCommand(subCommands, arguments[0]);
                yield subExecutor != null ?
                        subExecutor.suggest(player, Arrays.stream(arguments).skip(1).toArray(String[]::new)) :
                        List.of();
            }
        };
    }

    public @NotNull String buildHelpMessage(@NotNull String commandLabel,
                                            @NotNull String version,
                                            @NotNull List<RavenSubCommandHandler> subCommands,
                                            @NotNull RavenCommandParent.CommandColorScheme commandColorScheme) {
        val stringBuilder = new StringBuilder()
                .append("<newLine><").append(commandColorScheme.headerColor()).append(">")
                .append(commandLabel.toUpperCase())
                .append(" <gray>[v").append(version).append("]")
                .append("<newLine><newLine>");

        subCommands.forEach(subCommand ->
                stringBuilder.append("<").append(commandColorScheme.labelColor()).append(">/")
                        .append(commandLabel)
                        .append(" ")
                        .append(subCommand.label())
                        .append(" <gray>- <").append(commandColorScheme.descriptionColor()).append(">")
                        .append(subCommand.description())
                        .append("<newLine>")
        );

        stringBuilder.append("<newLine>");
        return stringBuilder.toString();
    }

    public @NotNull String buildHelpMessage(@NotNull String commandLabel,
                                            @NotNull String version,
                                            @NotNull List<RavenSubCommandHandler> subCommands) {
        return buildHelpMessage(commandLabel, version, subCommands, CommandColorScheme.DEFAULT);
    }

    @Accessors(fluent = true)
    public record CommandColorScheme(@NotNull String headerColor, @NotNull String labelColor,
                                     @NotNull String descriptionColor) {

        public static final @NotNull CommandColorScheme DEFAULT = new CommandColorScheme(
                "dark_purple", "light_purple", "white"
        );

    }

}