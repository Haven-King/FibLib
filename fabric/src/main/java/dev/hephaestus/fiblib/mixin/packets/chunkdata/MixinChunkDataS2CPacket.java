package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.impl.FibLib;
import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientboundLevelChunkPacket.class)
public class MixinChunkDataS2CPacket {
    @Unique
    private ServerPlayer player = null;

    @Inject(method = "<init>(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;getPos()Lnet/minecraft/world/level/ChunkPos;"))
    private void captureArgs(LevelChunk chunk, int includedSectionsMask, CallbackInfo ci) {
        FibLog.debug("Setting player for chunk %s", chunk.toString());
        if (FibLib.PLAYER.get() != null) {
            this.player = FibLib.PLAYER.get();
        }
    }

    @Inject(method = "calculateChunkSize", at = @At(value = "HEAD"))
    public void fixDataSize(LevelChunk chunk, int includedSectionsMark, CallbackInfoReturnable<Integer> cir) {
        String playerName = this.player == null ? "[Nobody]" : this.player.getName().getString();
        FibLog.debug("Maybe fixing chunk %s sections for %s", chunk.toString(), playerName);
        if (this.player != null) {
            for (LevelChunkSection chunkSection : chunk.getSections()) {
                if (chunkSection != null) {
                    ((Fixable) chunkSection).fix(this.player);
                }
            }
        }
    }
}
