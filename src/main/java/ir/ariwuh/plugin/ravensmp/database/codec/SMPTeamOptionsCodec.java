package ir.ariwuh.plugin.ravensmp.database.codec;

import ir.ariwuh.plugin.ravensmp.model.team.SMPTeamOptions;
import ir.ariwuh.plugin.ravensmp.utility.StringUtility;
import lombok.val;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public final class SMPTeamOptionsCodec implements Codec<SMPTeamOptions> {

    @Override
    public void encode(@NotNull BsonWriter writer,
                       @NotNull SMPTeamOptions value,
                       @NotNull EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeName("tagName");
        writer.writeString(value.tagName());

        writer.writeName("tagColor");
        writer.writeString(value.tagColor().toString());

        writer.writeName("friendlyFire");
        writer.writeBoolean(value.friendlyFire());

        writer.writeName("chatMuted");
        writer.writeBoolean(value.chatMuted());

        val homeLocation = value.homeLocation();
        writer.writeName("homeLocation");
        if (homeLocation != null) {
            writer.writeStartDocument();
            writer.writeName("world");
            writer.writeString(homeLocation.getWorld().getName());
            writer.writeName("x");
            writer.writeDouble(homeLocation.getX());
            writer.writeName("y");
            writer.writeDouble(homeLocation.getY());
            writer.writeName("z");
            writer.writeDouble(homeLocation.getZ());
            writer.writeName("yaw");
            writer.writeDouble(homeLocation.getYaw());
            writer.writeName("pitch");
            writer.writeDouble(homeLocation.getPitch());
            writer.writeEndDocument();
        } else writer.writeNull();

        writer.writeEndDocument();
    }

    @Override
    public @NotNull SMPTeamOptions decode(@NotNull BsonReader reader,
                                          @NotNull DecoderContext decoderContext) {
        reader.readStartDocument();

        val teamOptions = new SMPTeamOptions();

        while (!reader.readBsonType().equals(BsonType.END_OF_DOCUMENT)) {
            val fieldName = reader.readName();

            switch (fieldName) {
                case "tagName" -> teamOptions.tagName(reader.readString());
                case "tagColor" -> teamOptions.tagColor(StringUtility.stringToNamedTextColor(reader.readString()));
                case "friendlyFire" -> teamOptions.friendlyFire(reader.readBoolean());
                case "chatMuted" -> teamOptions.chatMuted(reader.readBoolean());
                case "homeLocation" -> {
                    val bsonType = reader.getCurrentBsonType();
                    if (bsonType.equals(BsonType.NULL)) {
                        reader.readNull();
                        teamOptions.homeLocation(null);
                    } else if (bsonType.equals(BsonType.DOCUMENT)) {
                        reader.readStartDocument();
                        String worldName = null;
                        double x = 0, y = 0, z = 0, yaw = 0, pitch = 0;

                        while (!reader.readBsonType().equals(BsonType.END_OF_DOCUMENT)) {
                            val locationField = reader.readName();
                            switch (locationField) {
                                case "world" -> worldName = reader.readString();
                                case "x", "y", "z", "yaw", "pitch" -> reader.readDouble();
                                default -> reader.skipValue();
                            }
                        }
                        reader.readEndDocument();

                        if (worldName != null) {
                            val world = Bukkit.getWorld(worldName);
                            teamOptions.homeLocation(
                                    world != null ?
                                            new Location(world, x, y, z, (float) yaw, (float) pitch) :
                                            null
                            );
                        }
                    } else reader.skipValue();

                }
                default -> reader.skipValue();
            }
        }

        reader.readEndDocument();

        return teamOptions;
    }

    @Override
    public @NotNull Class<SMPTeamOptions> getEncoderClass() {
        return SMPTeamOptions.class;
    }

}