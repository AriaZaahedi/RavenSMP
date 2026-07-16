package ir.ariwuh.plugin.ravensmp.database.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import ir.ariwuh.plugin.ravensmp.model.account.SMPAccount;
import ir.ariwuh.plugin.ravensmp.database.codec.SMPAccountCodec;
import ir.ariwuh.plugin.ravensmp.database.codec.SMPAccountSettingsCodec;
import ir.ariwuh.plugin.ravensmp.manager.DatabaseManager;
import ir.ariwuh.plugin.ravensmp.manager.LanguageManager;
import lombok.val;
import org.bson.BsonBinary;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public final class SMPAccountDao implements IDao<SMPAccount, UUID> {

    private final @NotNull MongoCollection<SMPAccount> collection;

    public SMPAccountDao(@NotNull DatabaseManager databaseManager, @NotNull LanguageManager languageManager) {
        val accountSettingsCodec = new SMPAccountSettingsCodec();
        this.collection = databaseManager.database()
                .getCollection("accounts", SMPAccount.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        CodecRegistries.fromCodecs(
                                new SMPAccountCodec(languageManager, accountSettingsCodec),
                                accountSettingsCodec
                        ),
                        DatabaseManager.DEFAULT_CODEC_REGISTRIES
                ));
    }

    @Override
    public void insert(@NotNull SMPAccount account) {
        this.collection.insertOne(account);
    }

    @Override
    public void update(@NotNull SMPAccount account) {
        this.collection.updateOne(
                Filters.eq("_id", new BsonBinary(account.accountId())),
                Updates.combine(
                        Updates.set("username", account.username()),
                        Updates.set("languageId", account.language().id()),
                        Updates.set("lastTeamCreationTime", account.lastTeamCreationTime()),
                        Updates.set("settings", account.accountSettings())
                )
        );
    }

    @Override
    public void delete(@NotNull UUID accountId) {
        this.collection.deleteOne(
                Filters.eq("_id", new BsonBinary(accountId))
        );
    }

    @Override
    public @Nullable SMPAccount findById(@NotNull UUID accountId) {
        return this.collection
                .find(Filters.eq("_id", new BsonBinary(accountId)))
                .first();
    }

    @Override
    public @NotNull Collection<SMPAccount> findAll() {
        return this.collection.find().into(new ArrayList<>());
    }

}