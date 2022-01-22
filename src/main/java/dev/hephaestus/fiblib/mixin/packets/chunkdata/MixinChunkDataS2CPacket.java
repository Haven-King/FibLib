package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.impl.FibLib;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

@Mixin(ChunkDataS2CPacket.class)
public class MixinChunkDataS2CPacket {

    @Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;Lnet/minecraft/world/chunk/light/LightingProvider;Ljava/util/BitSet;Ljava/util/BitSet;Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/ChunkData;<init>(Lnet/minecraft/world/chunk/WorldChunk;)V"))
    private void storeChunkSectionContext(WorldChunk chunk, LightingProvider lightProvider, BitSet skyBits, BitSet blockBits, boolean nonEdge, CallbackInfo ci) {
        if(FibLib.PLAYER.get() != null) {
            for (ChunkSection chunkSection : chunk.getSectionArray()) {
                if(chunkSection != null) {
                    ((Fixable) chunkSection).fix(FibLib.PLAYER.get());
                }
            }
        }
    }
}
