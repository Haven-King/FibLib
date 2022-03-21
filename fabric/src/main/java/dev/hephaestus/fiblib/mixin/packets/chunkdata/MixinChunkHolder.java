package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public class MixinChunkHolder {
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "method_13996(Lnet/minecraft/network/Packet;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("HEAD"))
    private static void fixPackets(Packet<?> packet, ServerPlayerEntity player, CallbackInfo ci) {
        if (packet instanceof Fixable) {
            ((Fixable) packet).fix(player);
        }
    }
}
