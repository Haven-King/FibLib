package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.FibLib;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
    @Shadow
    @Final
    private World world;

    @Inject(method = "setBlockState", at = @At("HEAD"))
    public void trackBlock(BlockPos pos, BlockState state, boolean bl, CallbackInfoReturnable<BlockState> cir) {
        FibLib.Blocks.track(world.getDimension().getType(), state, pos);
    }
}
