package dev.hephaestus.fiblib.mixin;

import com.qouteall.immersive_portals.chunk_loading.ChunkDataSyncManager;
import com.qouteall.immersive_portals.chunk_loading.DimensionalChunkPos;
import com.qouteall.immersive_portals.ducks.IEThreadedAnvilChunkStorage;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkDataSyncManager.class)
public interface CDSMAccessor {
    @Invoker
    void invokeSendChunkDataPacketNow(ServerPlayerEntity player, DimensionalChunkPos pos, IEThreadedAnvilChunkStorage tacs);
}
