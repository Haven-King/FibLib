package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.FibLib;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.BitSet;
import java.util.Random;

@Mixin(OreFeature.class)
public class OreFeatureMixin {
    @Inject(method = "generateVeinPart(Lnet/minecraft/world/IWorld;Ljava/util/Random;Lnet/minecraft/world/gen/feature/OreFeatureConfig;DDDDDDIIIII)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos$Mutable;set(III)Lnet/minecraft/util/math/BlockPos$Mutable;", by = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    public void registerOre(IWorld world, Random random, OreFeatureConfig config, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int size, int i, CallbackInfoReturnable<Boolean> cir, int j, BitSet bitSet, BlockPos.Mutable mutable) {
        if (FibLib.DEBUG) {
            if (world.getWorld() instanceof ServerWorld) {
                FibLib.Blocks.track(world.getDimension().getType(), config.state, new BlockPos(mutable));
            }
        }
    }
}
