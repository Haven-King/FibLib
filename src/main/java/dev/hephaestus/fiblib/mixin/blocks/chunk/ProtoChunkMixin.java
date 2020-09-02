package dev.hephaestus.fiblib.mixin.blocks.chunk;

import dev.hephaestus.fiblib.blocks.BlockTracker;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProtoChunk.class)
public class ProtoChunkMixin implements BlockTracker.Provider {
    @Unique
    private BlockTracker blockTracker;

    @Inject(method = "<init>(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/chunk/UpgradeData;[Lnet/minecraft/world/chunk/ChunkSection;Lnet/minecraft/world/ChunkTickScheduler;Lnet/minecraft/world/ChunkTickScheduler;)V", at = @At("RETURN"))
    private void initComponents(CallbackInfo ci) {
        this.blockTracker = new BlockTracker((Chunk) this);
    }

    @Override
    public BlockTracker getBlockTracker() {
        return this.blockTracker;
    }
}
