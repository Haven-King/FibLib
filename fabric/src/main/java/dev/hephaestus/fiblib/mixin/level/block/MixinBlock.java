package dev.hephaestus.fiblib.mixin.level.block;

import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.FibLog;
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

import java.util.List;

@Mixin(Block.class)
public abstract class MixinBlock {
    @Shadow
    public static List<ItemStack> getDrops(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack itemStack) {
        return null;
    }

    @Shadow
    public static void dropResources(BlockState blockState, Level level, BlockPos blockPos, BlockEntity blockEntity, Entity entity, ItemStack itemStack) {

    }

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    private static void injectDrops(BlockState blockState, Level level, BlockPos blockPos, BlockEntity blockEntity, Entity entity, ItemStack itemStack, CallbackInfo ci) {
        if (level instanceof ServerLevel && entity instanceof ServerPlayer) {
            @Nullable BlockFib fib = BlockFibRegistry.getBlockFib(blockState, (ServerPlayer) entity);

            // If the fib requests drop modifications & states do not match (currently being fibbed), cancel the original drop & drop the new loot table instead.
            if (fib != null) {
                if (fib.modifiesDrops()) {
                    BlockState newState = BlockFibRegistry.getBlockState(blockState, (ServerPlayer) entity);
                    if (newState != blockState) {
                        FibLog.debug("Modifying drops of %s to %s", blockState.getBlock().getName().getString(), newState.getBlock().getName().getString());
                        dropResources(newState, (ServerLevel) level, blockPos, blockEntity, entity, itemStack);

                        ci.cancel();
                    }
                }
            }
        }
    }
}
