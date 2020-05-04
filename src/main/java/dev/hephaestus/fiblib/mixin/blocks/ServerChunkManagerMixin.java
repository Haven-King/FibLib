package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.BlockTracker;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "method_20801(JZ[Lnet/minecraft/entity/EntityCategory;ZILit/unimi/dsi/fastutil/objects/Object2IntMap;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/server/world/ChunkHolder;)V", at = @At(value = "HEAD"))
    public void doThing(long l, boolean z0, EntityCategory [] e, boolean z1, int i0, Object2IntMap o0, BlockPos p0, int i1, ChunkHolder c0, CallbackInfo ci) {
        Optional<WorldChunk> optional = c0.getTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).left();
        BlockTracker tracker;
        if (optional.isPresent() && (tracker = FibLib.Blocks.TRACKER.get(optional.get())).getVersion() != FibLib.Blocks.getVersion()) {
            tracker.update();
        }
    }
}
