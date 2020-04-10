package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.Fibber;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkDataS2CPacket.class)
public class ChunkDataFibber implements Fibber {
    @Inject(method = "writeData(Lnet/minecraft/util/PacketByteBuf;Lnet/minecraft/world/chunk/WorldChunk;I)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/ChunkSection;toPacket(Lnet/minecraft/util/PacketByteBuf;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void fixInjection(PacketByteBuf packetByteBuf, WorldChunk chunk, int includedSectionsMask, CallbackInfoReturnable<Integer> cir, int i, ChunkSection[] chunkSections, int j, int k, ChunkSection chunkSection) {
        Fibber.fix(chunkSection, this.player);
    }

    @Shadow
    private int verticalStripBitmask;
    @Shadow public int writeData(PacketByteBuf packetByteBuf, WorldChunk chunk, int includedSectionsMask) {return 0;}
    @Shadow private ByteBuf getWriteBuffer() {return null;}

    private WorldChunk chunk;
    private int mask;
    @Inject(method = "<init>(Lnet/minecraft/world/chunk/WorldChunk;I)V", at = @At("TAIL"))
    public void initInjection(WorldChunk chunk, int includedSectionsMask, CallbackInfo ci) {
        this.chunk = chunk;
        this.mask = includedSectionsMask;
    }

    private ServerPlayerEntity player;
    @Override
    public void fix(ServerPlayerEntity player) {
        this.player = player;
        this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), chunk, mask);
    }
}
