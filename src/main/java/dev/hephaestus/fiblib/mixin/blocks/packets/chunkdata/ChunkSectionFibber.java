package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.Fibber;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkSection.class)
public class ChunkSectionFibber implements Fibber {
    @Mutable @Final @Shadow private final PalettedContainer<BlockState> container;

    public ChunkSectionFibber(PalettedContainer<BlockState> container) {
        this.container = container;
    }

    @Inject(method = "toPacket(Lnet/minecraft/util/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/PalettedContainer;toPacket(Lnet/minecraft/util/PacketByteBuf;)V"))
    public void fixInjection(PacketByteBuf packetByteBuf, CallbackInfo ci) {
        Fibber.fix(container, this.player);
    }

    private ServerPlayerEntity player;

    @Override
    public void fix(ServerPlayerEntity player) {
        this.player = player;
    }

}
