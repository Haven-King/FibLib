package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.impl.FibLib;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.chunk_loading.ChunkDataSyncManager;
import qouteall.imm_ptl.core.chunk_loading.DimensionalChunkPos;
import qouteall.imm_ptl.core.ducks.IEThreadedAnvilChunkStorage;

@Mixin(value = ChunkDataSyncManager.class, remap = false)
public class MixinChunkDataSyncManager {
    @Inject(method = "sendChunkDataPacketNow", at = @At("HEAD"))
    private void setPlayer(ServerPlayerEntity player, DimensionalChunkPos chunkPos, IEThreadedAnvilChunkStorage ieStorage, CallbackInfo ci) {
        FibLib.PLAYER.set(player);
    }
}
