package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.blocks.Fixable;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public class ChunkHolderFibber {
//    @Redirect(method = "flushUpdates", at = @At(value = "NEW", target = ""))
//    public void flushMyThings(WorldChunk worldChunk, CallbackInfo ci) {
//        this.playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, false).forEach((serverPlayerEntity) -> {
//            ChunkDataS2CPacket packet = new ChunkDataS2CPacket();
//            ChunkDataFibber.fix(packet).fix(worldChunk, this.sectionsNeedingUpdateMask, serverPlayerEntity);
//            serverPlayerEntity.networkHandler.sendPacket(packet);
//        });
//
//        this.blockUpdateCount = 0;
//        this.sectionsNeedingUpdateMask = 0;
//
//        ci.cancel();
//    }

    @Inject(method = "method_13996(Lnet/minecraft/network/Packet;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("HEAD"))
    private static void fixPackets(Packet<?> packet, ServerPlayerEntity player, CallbackInfo ci) {
        if (packet instanceof Fixable) {
            ((Fixable) packet).fix(player);
        }
    }
}
