package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.Fibber;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class PacketFibber {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow public abstract void sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericFutureListener);

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void fixPackets(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof BlockActionS2CPacket || packet instanceof PlayerActionResponseS2CPacket || packet instanceof BlockUpdateS2CPacket || packet instanceof ChunkDeltaUpdateS2CPacket || packet instanceof ChunkDataS2CPacket) {
            Fibber.fix(packet, player);
            this.sendPacket(packet, null);
            ci.cancel();
        }
    }
}
