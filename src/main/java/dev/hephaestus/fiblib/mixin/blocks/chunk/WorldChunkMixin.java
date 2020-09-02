package dev.hephaestus.fiblib.mixin.blocks.chunk;

import dev.hephaestus.fiblib.blocks.BlockTracker;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public class WorldChunkMixin implements BlockTracker.Provider {
    @Unique private BlockTracker blockTracker;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/source/BiomeArray;Lnet/minecraft/world/chunk/UpgradeData;Lnet/minecraft/world/TickScheduler;Lnet/minecraft/world/TickScheduler;J[Lnet/minecraft/world/chunk/ChunkSection;Ljava/util/function/Consumer;)V", at = @At("RETURN"))
    private void initComponents(CallbackInfo ci) {
        this.blockTracker = new BlockTracker((Chunk) this);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/chunk/ProtoChunk;)V", at = @At("RETURN"))
    private void copyFromProto(World world, ProtoChunk proto, CallbackInfo ci) {
        this.blockTracker = ((BlockTracker.Provider) proto).getBlockTracker();
    }

    @Override
    public BlockTracker getBlockTracker() {
        return this.blockTracker;
    }
}
