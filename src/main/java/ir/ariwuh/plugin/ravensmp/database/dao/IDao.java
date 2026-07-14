package ir.ariwuh.plugin.ravensmp.database.dao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface IDao<Entity, Id> {

    void insert(@NotNull Entity entity);

    void update(@NotNull Entity entity);

    void delete(@NotNull Id id);

    @Nullable Entity findById(@NotNull Id id);

    @NotNull Collection<Entity> findAll();

}