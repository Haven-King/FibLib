package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.blocks.ChunkTracker;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.Set;

@Mixin(ReadOnlyChunk.class)
public class ReadOnlyChunkMixin implements ChunkTracker {
    @Shadow @Final private WorldChunk wrapped;

    @Override
    public Set<Integer> trackedStates() {
        return ChunkTracker.inject(this.wrapped).trackedStates();
    }

    @Override
    public HashMap<Integer, LongSet> tracked() {
        return ChunkTracker.inject(this.wrapped).tracked();
    }

    @Override
    public LongSet tracked(BlockState state) {
        return ChunkTracker.inject(this.wrapped).tracked(state);
    }

    @Override
    public void track(BlockState state, BlockPos pos) {

    }

    @Override
    public int getVersion() {
        return ChunkTracker.inject(this.wrapped).getVersion();
    }

    @Override
    public void update() {
    }
}
