package dev.hephaestus.fiblib.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import qouteall.imm_ptl.core.chunk_loading.ChunkDataSyncManager;
import qouteall.imm_ptl.core.chunk_loading.DimensionalChunkPos;
import qouteall.imm_ptl.core.ducks.IEThreadedAnvilChunkStorage;

@Mixin(ChunkDataSyncManager.class)
public interface CDSMAccessor {
    @Invoker
    void invokeSendChunkDataPacketNow(ServerPlayerEntity player, DimensionalChunkPos pos, IEThreadedAnvilChunkStorage tacs);
}
