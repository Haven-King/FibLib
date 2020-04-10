package dev.hephaestus.fiblib.mixin;

import dev.hephaestus.fiblib.FibLib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(method = "onBlockRemoved", at = @At("HEAD"))
    public void removalInjection(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci) {
        if (world instanceof ServerWorld) FibLib.remove((ServerWorld)world, pos);
    }

    @Inject(method = "onBlockAdded", at = @At("HEAD"))
    public void addedInjection(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved, CallbackInfo ci) {
        if (world instanceof ServerWorld) FibLib.put(state.getBlock(), pos);
    }
}