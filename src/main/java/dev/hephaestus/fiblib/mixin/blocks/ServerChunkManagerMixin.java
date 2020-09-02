package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.BlockTracker;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "method_20801(JZLnet/minecraft/world/SpawnHelper$Info;ZILnet/minecraft/server/world/ChunkHolder;)V", at = @At(value = "HEAD"))
    public void doThing(long l, boolean b1, SpawnHelper.Info info, boolean b2, int i, ChunkHolder holder, CallbackInfo ci) {
        Optional<WorldChunk> optional = holder.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
        BlockTracker tracker;
        if (optional.isPresent() && (tracker = ((BlockTracker.Provider) optional.get()).getBlockTracker()).getVersion() != FibLib.Blocks.getVersion()) {
            tracker.update();
        }
    }
}
