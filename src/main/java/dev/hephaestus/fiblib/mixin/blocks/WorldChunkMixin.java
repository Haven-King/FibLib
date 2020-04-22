package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.ChunkTracker;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Set;

@Mixin(WorldChunk.class)
public class WorldChunkMixin implements ChunkTracker {
    @Shadow @Final private World world;
    private HashMap<Integer, LongSet> trackedBlocks = new HashMap<>();
    private int version = -1;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/ProtoChunk;)V", at = @At("TAIL"))
    public void copyTrackedBlocks(World world, ProtoChunk protoChunk, CallbackInfo ci) {
        this.trackedBlocks = ChunkTracker.inject(protoChunk).tracked();
    }

    @Inject(method = "setBlockState", at = @At("HEAD"))
    public void trackBlock(BlockPos pos, BlockState state, boolean bl, CallbackInfoReturnable<BlockState> cir) {
        track(state, pos);
    }

    @Override
    public Set<Integer> trackedStates() {
        return trackedBlocks.keySet();
    }

    @Override
    public HashMap<Integer, LongSet> tracked() {
        return trackedBlocks;
    }

    @Override
    public LongSet tracked(BlockState state) {
        return trackedBlocks.getOrDefault(Block.STATE_IDS.getId(state), new LongOpenHashSet());
    }

    @Override
    public void track(BlockState state, BlockPos pos) {
        long posLong = pos.asLong();
        Integer id = Block.STATE_IDS.getId(state);

        if (FibLib.Blocks.contains(state)) {
            int stateId = Block.STATE_IDS.getId(state);

            trackedBlocks.putIfAbsent(stateId, new LongOpenHashSet());
            trackedBlocks.get(stateId).add(pos.asLong());
        } else if (trackedBlocks.containsKey(id) && trackedBlocks.get(id).contains(posLong))
            trackedBlocks.get(id).remove(posLong);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void update() {
        for (LongSet s : trackedBlocks.values()) {
            for (Long l : s) {
                if (!this.world.isClient)
                    ((ServerWorld)this.world).getChunkManager().markForUpdate(BlockPos.fromLong(l));
            }
        }
        this.version = FibLib.Blocks.getVersion();
    }
}
