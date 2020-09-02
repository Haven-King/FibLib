package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.blocks.Fixable;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {
    @Shadow @Final private ServerLightingProvider serverLightingProvider;
    @Shadow @Final private LongSet loadedChunks;

    @Shadow @Final private Long2ObjectLinkedOpenHashMap<ChunkHolder> currentChunkHolders;

    @Shadow protected abstract Iterable<ChunkHolder> entryIterator();

    @Inject(method = "sendChunkDataPackets", at = @At("HEAD"))
    public void fixPackets(ServerPlayerEntity serverPlayerEntity_1, Packet<?>[] packets_1, WorldChunk worldChunk_1, CallbackInfo ci) {
        if (packets_1[0] == null) {
            packets_1[0] = new ChunkDataS2CPacket();
            Fixable.fix(packets_1[0]).fix(worldChunk_1, 65535, serverPlayerEntity_1);
            // this new boolean is apparently an "is invalid" flag.
            packets_1[1] = new LightUpdateS2CPacket(worldChunk_1.getPos(), this.serverLightingProvider, true);
        }
    }
}
