package dev.hephaestus.fiblib.fabric.mixin.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Platform;
import dev.hephaestus.fiblib.fabric.Fixable;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientboundLevelChunkPacket.class)
public class MixinClientLevelChunkDataPacket {
    @Unique
    private Player player = null;

    @Inject(method = "<init>(Lnet/minecraft/world/level/chunk/LevelChunk;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;getPos()Lnet/minecraft/world/level/ChunkPos;"))
    private void captureArgs(LevelChunk chunk, int includedSectionsMask, CallbackInfo ci) {
        FibLib.debug("Setting player for chunk %s", chunk.toString());
        if (Platform.getCurrentPlayer() != null) {
            this.player = Platform.getCurrentPlayer();
        }
    }

    @Inject(method = "calculateChunkSize", at = @At(value = "HEAD"))
    public void fixDataSize(LevelChunk chunk, int includedSectionsMark, CallbackInfoReturnable<Integer> cir) {
        if (this.player != null) {
            for (LevelChunkSection chunkSection : chunk.getSections()) {
                if (chunkSection != null) {
                    ((Fixable) chunkSection).fix(this.player);
                }
            }
        }
    }
}
