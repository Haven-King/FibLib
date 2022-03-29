package dev.hephaestus.fiblib.fabric.mixin;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Platform;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public abstract class MixinChunkMap {
    @Inject(method = "playerLoadedChunk", at = @At(value = "NEW", target = "net/minecraft/network/protocol/game/ClientboundLevelChunkPacket"))
    public void fixPackets(ServerPlayer player, Packet<?>[] packets, LevelChunk chunk, CallbackInfo ci) {
        FibLib.debug("Setting player %s for chunk %s", player.getName().getString(), chunk.toString());
        Platform.setCurrentPlayer(player);
    }
}
