package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.blocks.ChunkDataFibber;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public class ChunkHolderFibber {
    @Shadow @Final private ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider;
    @Shadow @Final private ChunkPos pos;
    @Shadow private int blockUpdateCount;
    @Shadow private int sectionsNeedingUpdateMask;

    @Inject(method = "flushUpdates", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ChunkHolder;sendPacketToPlayersWatching(Lnet/minecraft/network/Packet;Z)V", ordinal = 3), cancellable = true)
    public void flushMyThings(WorldChunk worldChunk, CallbackInfo ci) {
        this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, false).forEach((serverPlayerEntity) -> {
            ChunkDataS2CPacket packet = new ChunkDataS2CPacket();
            ChunkDataFibber.fix(packet).fix(worldChunk, this.sectionsNeedingUpdateMask, serverPlayerEntity);
            serverPlayerEntity.networkHandler.sendPacket(packet);
        });

        this.blockUpdateCount = 0;
        this.sectionsNeedingUpdateMask = 0;

        ci.cancel();
    }
}
