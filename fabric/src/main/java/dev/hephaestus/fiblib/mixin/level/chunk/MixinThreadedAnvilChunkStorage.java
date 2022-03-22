package dev.hephaestus.fiblib.mixin.level.chunk;

import dev.hephaestus.fiblib.impl.FibLib;
import dev.hephaestus.fiblib.impl.FibLog;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public abstract class MixinThreadedAnvilChunkStorage {
    @Inject(method = "playerLoadedChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundLevelChunkPacket;<init>(Lnet/minecraft/world/level/chunk/LevelChunk;I)V"))
    public void fixPackets(ServerPlayer player, Packet<?>[] packets, LevelChunk chunk, CallbackInfo ci) {
        FibLog.debug("Setting player %s for chunk %s", player.getName().getString(), chunk.toString());
        FibLib.PLAYER.set(player);
    }
}
