package ir.ariwuh.plugin.ravensmp.command.api;

import lombok.val;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class SubCommandHandler {

    private final SubCommand subCommand;

    public SubCommandHandler() {
        this.subCommand = getClass().getDeclaredAnnotation(SubCommand.class);
        Objects.requireNonNull(this.subCommand, "Subcommands must have annotation!");
    }

    public abstract void execute(@NotNull Player player, @NotNull String[] arguments);

    public @NotNull Collection<String> suggest(@NotNull Player player, @NotNull String[] arguments) {
        return List.of();
    }

    @SuppressWarnings("unchecked")
    public <T extends Collection<String>> @NotNull T suggestWithStartingPrefix(@NotNull T strings,
                                                                               @NotNull String[] arguments) {
        if (arguments.length == 0) return strings;

        val lastArgument = arguments[arguments.length - 1].toLowerCase();
        return (T) strings.stream()
                .filter(text -> text.startsWith(lastArgument))
                .collect(Collectors.toList());
    }

    public @NotNull String label() {
        return this.subCommand.label();
    }

    public @NotNull String description() {
        return this.subCommand.description();
    }

}