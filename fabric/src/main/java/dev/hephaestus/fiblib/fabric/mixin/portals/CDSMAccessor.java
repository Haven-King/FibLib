package dev.hephaestus.fiblib.fabric.mixin.portals;

import com.qouteall.immersive_portals.chunk_loading.ChunkDataSyncManager;
import com.qouteall.immersive_portals.chunk_loading.DimensionalChunkPos;
import com.qouteall.immersive_portals.ducks.IEThreadedAnvilChunkStorage;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkDataSyncManager.class)
public interface CDSMAccessor {
    @Invoker
    void invokeSendChunkDataPacketNow(ServerPlayer player, DimensionalChunkPos pos, IEThreadedAnvilChunkStorage tacs);
}
