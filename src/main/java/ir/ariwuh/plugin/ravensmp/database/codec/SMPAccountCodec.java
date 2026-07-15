package ir.ariwuh.plugin.ravensmp.database.codec;

import ir.ariwuh.plugin.ravensmp.account.SMPAccount;
import ir.ariwuh.plugin.ravensmp.account.SMPAccountSettings;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@RequiredArgsConstructor
public final class SMPAccountCodec implements Codec<SMPAccount> {

    private final @NotNull SMPAccountSettingsCodec accountSettingsCodec;

    @Override
    public void encode(@NotNull BsonWriter writer,
                       @NotNull SMPAccount value,
                       @NotNull EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeName("_id");
        writer.writeBinaryData(new BsonBinary(value.accountId()));

        writer.writeString("username", value.username());

        writer.writeName("settings");
        this.accountSettingsCodec.encode(writer, value.accountSettings(), encoderContext);

        writer.writeEndDocument();
    }

    @Override
    public @Nullable SMPAccount decode(@NotNull BsonReader reader,
                                       @NotNull DecoderContext decoderContext) {
        reader.readStartDocument();

        UUID accountId = null;
        String username = null;
        SMPAccountSettings accountSettings = null;

        while (!reader.readBsonType().equals(BsonType.END_OF_DOCUMENT)) {
            val fieldName = reader.readName();

            switch (fieldName) {
                case "_id" -> accountId = reader.readBinaryData().asUuid();
                case "username" -> username = reader.readString();
                case "settings" -> accountSettings = this.accountSettingsCodec.decode(reader, decoderContext);
                default -> reader.skipValue();
            }
        }

        reader.readEndDocument();

        if (accountId == null || username == null) return null;

        val account = new SMPAccount(accountId, username);
        account.accountSettings(accountSettings != null ? accountSettings : SMPAccountSettings.defaultSettings());
        return account;
    }

    @Override
    public @NotNull Class<SMPAccount> getEncoderClass() {
        return SMPAccount.class;
    }

}