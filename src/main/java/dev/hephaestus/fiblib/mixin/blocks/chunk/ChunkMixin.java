package dev.hephaestus.fiblib.mixin.blocks.chunk;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.BlockTracker;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WorldChunk.class, ProtoChunk.class})
public class ChunkMixin {
    @Inject(method = "setBlockState", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/chunk/ChunkSection;setBlockState(IIILnet/minecraft/block/BlockState;)Lnet/minecraft/block/BlockState;"))
    public void trackBlock(BlockPos pos, BlockState state, boolean bl, CallbackInfoReturnable<BlockState> cir) {
        if (FibLib.Blocks.contains(state)) {
            ((BlockTracker.Provider) this).getBlockTracker().track(state, pos);
        }
    }
}
