package ir.ariwuh.plugin.ravensmp.config;

import ir.ariwuh.plugin.ravensmp.RavenSMPPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class PluginConfigFile {

    private static final @NotNull RavenSMPPlugin PLUGIN = RavenSMPPlugin.instance();
    @Getter
    private final @NotNull File file;

    @Getter
    private FileConfiguration config;

    public PluginConfigFile(@NotNull String fileName) {
        this(PLUGIN.getDataFolder(), fileName);
    }

    public PluginConfigFile(@NotNull File parent, @NotNull String childName) {
        this(new File(parent, childName + ".yml"));
    }

    @SneakyThrows
    public void createFile() {
        if (exists()) return;
        createPluginDataFolder();
        if (this.file.createNewFile()) return;

        PLUGIN.getLogger().severe(
                "[ERROR] Failed to create file '%s'. Disabling plugin..."
                        .formatted(this.file.getName())
        );
        PLUGIN.disablePlugin();
    }

    public boolean copyFromResource(@NotNull String fileName,
                                    @NotNull Consumer<IOException> whenFailed) {
        createPluginDataFolder();
        val toBePlaced = new File(PLUGIN.getDataFolder(), fileName + ".yml");
        return copyFromResource(toBePlaced, fileName, whenFailed);
    }

    public boolean copyFromResource(@NotNull File toBePlaced,
                                    @NotNull String fileName,
                                    @NotNull Consumer<IOException> whenFailed) {
        createPluginDataFolder();
        val inputStream = PLUGIN.getResource(fileName + ".yml");
        if (inputStream == null) return false;

        try (inputStream) {
            Files.copy(inputStream, toBePlaced.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException exception) {
            whenFailed.accept(exception);
            return false;
        }
    }

    public void load() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public void set(@NotNull String path, @Nullable Object value) {
        this.config.set(path, value);
    }

    @SneakyThrows
    public void save() {
        if (!exists()) return;
        this.config.save(this.file);
    }

    public boolean exists() {
        return this.file.exists();
    }

    private void createPluginDataFolder() {
        val pluginDataFolder = PLUGIN.getDataFolder();
        if (pluginDataFolder.exists()) return;
        if (pluginDataFolder.mkdir()) return;

        PLUGIN.getLogger().severe(
                "[Config] [ERROR] Failed to create plugin data folder. Disabling plugin..."
        );
        PLUGIN.disablePlugin();
    }

}