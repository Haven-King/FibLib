package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.Fibber;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PalettedContainer.class)
public class PalettedContainerFibber<T> implements Fibber {
    @Shadow private Palette<T> palette;

    @Inject(method = "toPacket(Lnet/minecraft/util/PacketByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Palette;toPacket(Lnet/minecraft/util/PacketByteBuf;)V"))
    public void fixInjection(PacketByteBuf buf, CallbackInfo ci) {
        Fibber.fix(palette, this.player);
    }

    private ServerPlayerEntity player;

    @Override
    public void fix(ServerPlayerEntity player) {
        this.player = player;
    }
}
