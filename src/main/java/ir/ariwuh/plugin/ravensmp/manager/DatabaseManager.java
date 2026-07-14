package ir.ariwuh.plugin.ravensmp.manager;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ir.ariwuh.plugin.ravensmp.config.PluginSettings;
import lombok.val;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class DatabaseManager {

    public static final @NotNull CodecRegistry DEFAULT_CODEC_REGISTRIES = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(
                    PojoCodecProvider.builder().automatic(true).build()
            )
    );

    private final @NotNull PluginSettings pluginSettings;
    private final @NotNull MongoClient mongoClient;

    public DatabaseManager(@NotNull PluginSettings pluginSettings) {
        this.pluginSettings = pluginSettings;
        val mongoClientSettings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(new ConnectionString(pluginSettings.databaseUri()))
                .codecRegistry(DEFAULT_CODEC_REGISTRIES)
                .build();

        this.mongoClient = MongoClients.create(mongoClientSettings);
    }

    public void close() {
        this.mongoClient.close();
    }

    @Contract(pure = true)
    public @NotNull MongoDatabase database() {
        return this.mongoClient.getDatabase(this.pluginSettings.databaseName());
    }

}