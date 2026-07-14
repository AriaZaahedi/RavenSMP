package ir.ariwuh.plugin.ravensmp.database.codec;

import ir.ariwuh.plugin.ravensmp.team.SMPTeamMember;
import lombok.val;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

public final class SMPTeamMemberCodec implements Codec<SMPTeamMember> {

    @Override
    public void encode(@NotNull BsonWriter writer,
                       @NotNull SMPTeamMember value,
                       @NotNull EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeName("playerId");
        writer.writeBinaryData(new BsonBinary(value.playerId()));

        writer.writeName("username");
        writer.writeString(value.username());

        writer.writeEndDocument();
    }

    @Override
    public @NotNull SMPTeamMember decode(@NotNull BsonReader reader,
                                         @NotNull DecoderContext decoderContext) {
        reader.readStartDocument();

        val teamMember = new SMPTeamMember();

        while (!reader.readBsonType().equals(BsonType.END_OF_DOCUMENT)) {
            val fieldName = reader.readName();

            switch (fieldName) {
                case "playerId" -> teamMember.playerId(reader.readBinaryData().asUuid());
                case "username" -> teamMember.username(reader.readString());
                default -> reader.skipValue();
            }
        }

        reader.readEndDocument();

        return teamMember;
    }

    @Override
    public @NotNull Class<SMPTeamMember> getEncoderClass() {
        return SMPTeamMember.class;
    }

}