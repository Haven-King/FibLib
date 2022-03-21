package dev.hephaestus.fiblib.mixin;

import dev.hephaestus.fiblib.impl.Fixable;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {
    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract void sendPacket(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericFutureListener);

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void fixPackets(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof Fixable) {
            ((Fixable) packet).fix(this.player);
            this.sendPacket(packet, null);
            ci.cancel();
        }
    }
}
