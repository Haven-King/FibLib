package dev.hephaestus.fiblib.mixin;

import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.Fixable;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerPlayNetworkHandler {
    @Shadow
    public ServerPlayer player;

    @Shadow
    public abstract void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> genericFutureListener);

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void fixPackets(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof Fixable) {
            FibLog.debug("Fixing packet %s for %s", packet.toString(), player.getName().getString());
            ((Fixable) packet).fix(this.player);
            this.send(packet, null);
            ci.cancel();
        }
    }
}
