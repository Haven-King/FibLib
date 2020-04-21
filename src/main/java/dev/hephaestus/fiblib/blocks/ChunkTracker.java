package dev.hephaestus.fiblib.blocks;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.Chunk;

import java.util.Map;
import java.util.Set;

public interface ChunkTracker {
    static ChunkTracker inject(Chunk chunk) {
        return (ChunkTracker)chunk;
    }
    Set<Integer> trackedStates();
    Set<Map.Entry<Integer, LongSet>> tracked();
    LongSet tracked(BlockState state);
    void track(BlockState state, long pos);
    void track(Integer stateId, long pos);
}
