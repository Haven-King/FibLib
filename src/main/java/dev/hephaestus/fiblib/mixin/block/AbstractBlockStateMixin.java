package dev.hephaestus.fiblib.mixin.block;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow protected abstract BlockState asBlockState();

    @Inject(
            method = "calcBlockBreakingDelta",
            at = @At("HEAD"), cancellable = true)
    private void onCalculateDelta(PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if(world instanceof ServerWorld) {
            BlockState newState = BlockFibRegistry.getBlockState(asBlockState(), (ServerPlayerEntity) player);
            cir.setReturnValue(newState.getBlock().calcBlockBreakingDelta(newState, player, world, pos));
        }
    }
}
