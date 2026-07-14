package ir.ariwuh.plugin.ravensmp.database.codec;

import ir.ariwuh.plugin.ravensmp.team.SMPTeam;
import ir.ariwuh.plugin.ravensmp.team.SMPTeamMember;
import ir.ariwuh.plugin.ravensmp.team.SMPTeamOptions;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class SMPTeamCodec implements Codec<SMPTeam> {

    private final @NotNull SMPTeamOptionsCodec teamOptionsCodec;
    private final @NotNull SMPTeamMemberCodec teamMemberCodec;

    @Override
    public void encode(@NotNull BsonWriter writer,
                       @NotNull SMPTeam value,
                       @NotNull EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeName("_id");
        writer.writeString(value.teamId());

        writer.writeName("teamLeader");
        this.teamMemberCodec.encode(writer, (SMPTeamMember) value.teamLeader(), encoderContext);

        writer.writeName("teamMembers");
        writer.writeStartArray();
        value.teamMembers().forEach(
                member -> this.teamMemberCodec.encode(writer, (SMPTeamMember) member, encoderContext)
        );
        writer.writeEndArray();

        writer.writeName("teamOptions");
        this.teamOptionsCodec.encode(writer, (SMPTeamOptions) value.teamOptions(), encoderContext);

        writer.writeEndDocument();
    }

    @Override
    public @NotNull SMPTeam decode(@NotNull BsonReader reader,
                                   @NotNull DecoderContext decoderContext) {
        reader.readStartDocument();

        val teamId = reader.readString("_id");

        reader.readName("teamLeader");
        val teamLeader = this.teamMemberCodec.decode(reader, decoderContext);

        val team = new SMPTeam(teamId, teamLeader);

        reader.readName("teamMembers");
        reader.readStartArray();
        while (!reader.readBsonType().equals(BsonType.END_OF_DOCUMENT)) {
            val member = this.teamMemberCodec.decode(reader, decoderContext);
            team.addMember(member);
        }
        reader.readEndArray();

        reader.readName("teamOptions");
        val teamOptions = this.teamOptionsCodec.decode(reader, decoderContext);
        teamOptions.smpTeam(team);
        team.teamOptions(teamOptions);

        reader.readEndDocument();

        return team;
    }

    @Override
    public @NotNull Class<SMPTeam> getEncoderClass() {
        return SMPTeam.class;
    }

}