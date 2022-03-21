package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.impl.FibLib;
import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkDataS2CPacket.class)
public class MixinChunkDataS2CPacket {
    @Unique private ServerPlayerEntity player = null;

    @Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;getPos()Lnet/minecraft/util/math/ChunkPos;"))
    private void captureArgs(WorldChunk chunk, int includedSectionsMask, CallbackInfo ci) {
        if (FibLib.PLAYER.get() != null) {
            this.player = FibLib.PLAYER.get();
        }
    }

    @Inject(method = "getDataSize", at = @At(value = "HEAD"))
    public void fixDataSize(WorldChunk chunk, int includedSectionsMark, CallbackInfoReturnable<Integer> cir) {
        if (this.player != null) {
            for (ChunkSection chunkSection : chunk.getSectionArray()) {
                if (chunkSection != null) {
                    ((Fixable) chunkSection).fix(this.player);
                }
            }
        }
    }
}
