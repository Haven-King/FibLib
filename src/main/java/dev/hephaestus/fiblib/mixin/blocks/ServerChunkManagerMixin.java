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
    @Inject(remap=false, method = "method_20801(JZLnet/minecraft/world/SpawnHelper$Info;ZILnet/minecraft/server/world/ChunkHolder;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V", at = @At(value = "HEAD"))
    public void doThing(long l, boolean b1, SpawnHelper.Info info, boolean b2, int i, ChunkHolder c0, CallbackInfo ci) {
        Optional<WorldChunk> optional = c0.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
        BlockTracker tracker;
        if (optional.isPresent() && (tracker = FibLib.Blocks.TRACKER.get(optional.get())).getVersion() != FibLib.Blocks.getVersion()) {
            tracker.update();
        }
    }
}
