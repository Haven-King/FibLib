package dev.hephaestus.fiblib.mixin.blocks.chunk;

import dev.hephaestus.fiblib.blocks.BlockTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
        @Inject(method = "deserialize", at = @At("RETURN"))
        private static void deserialize(ServerWorld world, StructureManager structureManager, PointOfInterestStorage poiStorage, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir) {
            ProtoChunk ret = cir.getReturnValue();
            Chunk chunk = ret instanceof ReadOnlyChunk ? ((ReadOnlyChunk) ret).getWrappedChunk() : ret;
            CompoundTag levelData = tag.getCompound("Level");
            ((BlockTracker.Provider) chunk).getBlockTracker().fromTag(levelData.getCompound("FibLibBlockTracker"));
        }

        @Inject(method = "serialize", at = @At("RETURN"))
        private static void serialize(ServerWorld world, Chunk chunk, CallbackInfoReturnable<CompoundTag> cir) {
            CompoundTag ret = cir.getReturnValue();
            CompoundTag levelData = ret.getCompound("Level");
            levelData.put("FibLibBlockTracker", ((BlockTracker.Provider) chunk).getBlockTracker().toTag(new CompoundTag()));
        }
    }
