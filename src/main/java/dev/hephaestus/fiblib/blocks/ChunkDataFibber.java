package dev.hephaestus.fiblib.blocks;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.WorldChunk;

public interface ChunkDataFibber {
    static ChunkDataFibber fix(Object object) {
        return (ChunkDataFibber)object;
    }

    void fix(WorldChunk chunk, int includedSectionsMask, ServerPlayerEntity player);
}
