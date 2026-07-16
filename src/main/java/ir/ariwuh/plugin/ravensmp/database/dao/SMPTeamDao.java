package ir.ariwuh.plugin.ravensmp.database.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import ir.ariwuh.plugin.ravensmp.database.codec.SMPTeamCodec;
import ir.ariwuh.plugin.ravensmp.database.codec.SMPTeamMemberCodec;
import ir.ariwuh.plugin.ravensmp.database.codec.SMPTeamOptionsCodec;
import ir.ariwuh.plugin.ravensmp.manager.DatabaseManager;
import ir.ariwuh.plugin.ravensmp.model.team.SMPTeam;
import lombok.val;
import org.bson.codecs.configuration.CodecRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public final class SMPTeamDao implements IDao<SMPTeam, String> {

    private final @NotNull MongoCollection<SMPTeam> collection;

    public SMPTeamDao(@NotNull DatabaseManager databaseManager) {
        val teamOptionsCodec = new SMPTeamOptionsCodec();
        val teamMemberCodec = new SMPTeamMemberCodec();
        val teamCodec = new SMPTeamCodec(teamOptionsCodec, teamMemberCodec);

        this.collection = databaseManager.database()
                .getCollection("teams", SMPTeam.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(
                        CodecRegistries.fromCodecs(teamCodec, teamOptionsCodec, teamMemberCodec),
                        DatabaseManager.DEFAULT_CODEC_REGISTRIES
                ));
    }

    @Override
    public void insert(@NotNull SMPTeam team) {
        this.collection.insertOne(team);
    }

    @Override
    public void update(@NotNull SMPTeam team) {
        this.collection.updateOne(
                Filters.eq("_id", team.teamId()),
                Updates.combine(
                        Updates.set("teamLeader", team.teamLeader()),
                        Updates.set("teamMembers", team.teamMembers())
                )
        );
    }

    @Override
    public void delete(@NotNull String teamId) {
        this.collection.deleteOne(
                Filters.eq("_id", teamId)
        );
    }

    @Override
    public @Nullable SMPTeam findById(@NotNull String teamId) {
        return this.collection
                .find(Filters.eq("_id", teamId))
                .first();
    }

    @Override
    public @NotNull Collection<SMPTeam> findAll() {
        return this.collection
                .find()
                .into(new ArrayList<>());
    }

}