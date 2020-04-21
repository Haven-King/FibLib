package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.ChunkTracker;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Mixin(ProtoChunk.class)
public class ProtoChunkMixin implements ChunkTracker {
    private final HashMap<Integer, LongSet> trackedBlocks = new HashMap<>();

    @Inject(method = "setBlockState", at = @At("HEAD"))
    public void trackBlock(BlockPos pos, BlockState state, boolean bl, CallbackInfoReturnable<BlockState> cir) {
        long posLong = pos.asLong();
        Integer id = Block.STATE_IDS.getId(state);
        if (FibLib.Blocks.contains(state))
            track(state, posLong);
        else if (trackedBlocks.containsKey(id) && trackedBlocks.get(id).contains(posLong))
            trackedBlocks.get(id).remove(posLong);
    }

    @Override
    public Set<Integer> trackedStates() {
        return trackedBlocks.keySet();
    }

    @Override
    public Set<Map.Entry<Integer, LongSet>> tracked() {
        return trackedBlocks.entrySet();
    }

    @Override
    public LongSet tracked(BlockState state) {
        Integer id = Block.STATE_IDS.getId(state);
        trackedBlocks.putIfAbsent(id, new LongOpenHashSet());
        return trackedBlocks.get(id);
    }

    @Override
    public void track(BlockState state, long pos) {
        track(Block.STATE_IDS.getId(state), pos);
    }

    @Override
    public void track(Integer stateId, long pos) {
        trackedBlocks.putIfAbsent(stateId, new LongOpenHashSet());
        trackedBlocks.get(stateId).add(pos);
    }
}
