package ir.ariwuh.plugin.ravensmp.database.codec;

import ir.ariwuh.plugin.ravensmp.account.SMPAccountSettings;
import lombok.val;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class SMPAccountSettingsCodec implements Codec<SMPAccountSettings> {

    @Override
    public void encode(@NotNull BsonWriter writer,
                       @NotNull SMPAccountSettings value,
                       @NotNull EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeBoolean("teamInvitesDisabled", value.teamInvitesDisabled());

        writer.writeEndDocument();
    }

    @Override
    public @NonNull SMPAccountSettings decode(@NotNull BsonReader reader,
                                              @NotNull DecoderContext decoderContext) {
        reader.readStartDocument();

        boolean teamInvitesDisabled = false;

        while (!reader.readBsonType().equals(BsonType.END_OF_DOCUMENT)) {
            val fieldName = reader.readName();

            switch (fieldName) {
                case "teamInvitesDisabled" -> teamInvitesDisabled = reader.readBoolean();
                default -> reader.skipValue();
            }
        }

        reader.readEndDocument();

        val accountSettings = new SMPAccountSettings();
        accountSettings.teamInvitesDisabled(teamInvitesDisabled);

        return accountSettings;
    }

    @Override
    public @NotNull Class<SMPAccountSettings> getEncoderClass() {
        return SMPAccountSettings.class;
    }

}