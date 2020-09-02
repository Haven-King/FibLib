package dev.hephaestus.fiblib.blocks;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.WorldChunk;

public interface Fixable {
    static <T> T fix(T object, ServerPlayerEntity player) {
        if (object instanceof Fixable) {
            ((Fixable) object).fix(player);
        }

        return object;
    }

    static Fixable fix(Object object) {
        return (Fixable)object;
    }

    void fix(WorldChunk chunk, int includedSectionsMask, ServerPlayerEntity player);

    default void fix(ServerPlayerEntity player) {
        this.fix(null, 0, player);
    }
}
