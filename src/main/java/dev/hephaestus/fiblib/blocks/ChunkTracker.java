package dev.hephaestus.fiblib.blocks;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Set;

public interface ChunkTracker {
    static ChunkTracker inject(Chunk chunk) {
        return (ChunkTracker)chunk;
    }
    Set<Integer> trackedStates();
    HashMap<Integer, LongSet> tracked();
    LongSet tracked(BlockState state);
    void track(BlockState state, BlockPos pos);
    int getVersion();
    void update();
}
