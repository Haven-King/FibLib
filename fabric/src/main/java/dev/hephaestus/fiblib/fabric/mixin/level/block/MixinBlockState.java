package dev.hephaestus.fiblib.fabric.mixin.level.block;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.impl.LookupImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class MixinBlockState {
    @Shadow
    protected BlockState asState() {
        return null;
    }

    @Inject(method = "getDestroyProgress", at = @At("HEAD"), cancellable = true)
    public void getDestroyProgress(Player player, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
        if (blockGetter instanceof ServerLevel) {
            BlockState newState = LookupImpl.findState(asState(), player, false);

            FibLib.debug("Fibbing destroyProgress for %s to match %s", asState().getBlock().getName().getString(), newState.getBlock().getName().getString());
            cir.setReturnValue(newState.getBlock().getDestroyProgress(newState, player, blockGetter, blockPos));
        }
    }
}
