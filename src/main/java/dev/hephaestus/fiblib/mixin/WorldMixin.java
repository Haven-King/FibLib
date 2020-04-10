package dev.hephaestus.fiblib.mixin;

import dev.hephaestus.fiblib.FibLib;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class WorldMixin {
    @Mutable @Final @Shadow private final World world;

    public WorldMixin(World world) {
        this.world = world;
    }

    @Inject(method = "setBlockState", at = @At("HEAD"))
    public void setBlockStateInjection(BlockPos pos, BlockState state, boolean bl, CallbackInfoReturnable<BlockState> cir) {
        if (world instanceof ServerWorld) FibLib.put((ServerWorld)world, pos);
    }
}
