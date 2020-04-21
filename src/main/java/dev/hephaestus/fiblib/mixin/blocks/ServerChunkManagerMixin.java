package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.FibLib;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
    @Inject(method = "method_20801(JZ[Lnet/minecraft/entity/EntityCategory;ZILit/unimi/dsi/fastutil/objects/Object2IntMap;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/server/world/ChunkHolder;)V", at = @At("HEAD"))
    public void doThing(long ignored0, boolean ignored1, EntityCategory[] ignored2, boolean ignored3, int ignored4, Object2IntMap<EntityCategory> ignored5, BlockPos ignored6, int ignored7, ChunkHolder ignored8, CallbackInfo ci) {

    }
}
