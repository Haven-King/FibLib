package dev.hephaestus.fiblib.mixin.portals;

import com.qouteall.immersive_portals.chunk_loading.ChunkDataSyncManager;
import com.qouteall.immersive_portals.chunk_loading.DimensionalChunkPos;
import com.qouteall.immersive_portals.ducks.IEThreadedAnvilChunkStorage;
import dev.hephaestus.fiblib.impl.FibLib;
import dev.hephaestus.fiblib.impl.FibLog;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkDataSyncManager.class, remap = false)
public class MixinChunkDataSyncManager {
    @Inject(method = "sendChunkDataPacketNow", at = @At("HEAD"))
    private void setPlayer(ServerPlayer player, DimensionalChunkPos chunkPos, IEThreadedAnvilChunkStorage ieStorage, CallbackInfo ci) {
        FibLib.PLAYER.set(player);
    }
}