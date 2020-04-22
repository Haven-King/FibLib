package dev.hephaestus.fiblib.blocks;

import net.minecraft.server.world.ChunkHolder;

public interface ChunkStorageAccessor {
    Iterable<ChunkHolder> getLoadedChunks();
}
