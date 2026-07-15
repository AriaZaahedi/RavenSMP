package ir.ariwuh.plugin.ravensmp.database.codec;

import ir.ariwuh.plugin.ravensmp.account.SMPAccount;
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

public final class SMPAccountCodec implements Codec<SMPAccount> {

    @Override
    public void encode(@NotNull BsonWriter writer,
                       @NotNull SMPAccount value,
                       @NotNull EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeName("_id");
        writer.writeBinaryData(new BsonBinary(value.accountId()));

        writer.writeString("username", value.username());

        writer.writeEndDocument();
    }

    @Override
    public @Nullable SMPAccount decode(@NotNull BsonReader reader,
                                       @NotNull DecoderContext decoderContext) {
        reader.readStartDocument();

        UUID accountId = null;
        String username = null;

        while (!reader.readBsonType().equals(BsonType.END_OF_DOCUMENT)) {
            val fieldName = reader.readName();

            switch (fieldName) {
                case "_id" -> accountId = reader.readBinaryData().asUuid();
                case "username" -> username = reader.readString();
                default -> reader.skipValue();
            }
        }

        reader.readEndDocument();

        if (accountId == null || username == null) return null;

        return new SMPAccount(accountId, username);
    }

    @Override
    public @NotNull Class<SMPAccount> getEncoderClass() {
        return SMPAccount.class;
    }

}