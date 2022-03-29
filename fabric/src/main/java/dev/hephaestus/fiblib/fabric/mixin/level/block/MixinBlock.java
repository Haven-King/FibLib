package dev.hephaestus.fiblib.fabric.mixin.level.block;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.impl.LookupImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlock {
    @Shadow
    public static void dropResources(BlockState blockState, Level level, BlockPos blockPos, BlockEntity blockEntity, Entity entity, ItemStack itemStack) {}

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void injectDrops(BlockState blockState, Level level, BlockPos blockPos, BlockEntity blockEntity, Entity entity, ItemStack itemStack, CallbackInfo ci) {
        if (level instanceof ServerLevel && entity instanceof ServerPlayer) {
            BlockState newState = LookupImpl.findDropsState(blockState, (ServerPlayer) entity);
            if (newState != blockState) {
                FibLib.debug("Modifying drops of %s to %s", blockState.getBlock().getName().getString(), newState.getBlock().getName().getString());
                dropResources(newState, level, blockPos, blockEntity, entity, itemStack);

                ci.cancel();
            }
        }
    }
}
