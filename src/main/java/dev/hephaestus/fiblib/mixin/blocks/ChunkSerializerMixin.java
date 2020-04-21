package dev.hephaestus.fiblib.mixin.blocks;

import dev.hephaestus.fiblib.blocks.ChunkTracker;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

import static dev.hephaestus.fiblib.blocks.ChunkTracker.inject;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @Inject(method = "serialize", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void saveTracked(ServerWorld serverWorld, Chunk chunk, CallbackInfoReturnable<CompoundTag> cir,
                                    ChunkPos chunkPos, CompoundTag compoundTag, CompoundTag compoundTag2) {
        CompoundTag trackedStates = new CompoundTag();
        ChunkTracker tracker = inject(chunk);
        for (Map.Entry<Integer, LongSet> entry : tracker.tracked()) {
            LongArrayTag trackedBlocks = new LongArrayTag(entry.getValue());
            trackedStates.put(entry.getKey().toString(), trackedBlocks);
        }

        int[] tracked = new int[tracker.trackedStates().size()];
        int i = 0;
        for (Integer integer : tracker.trackedStates())
            tracked[i++] = integer;

        trackedStates.putIntArray("TrackedStates", tracked);

        compoundTag2.put("TrackedBlocks", trackedStates);
    }

    @Inject(method = "deserialize", at = @At(value = "JUMP", opcode = Opcodes.IF_ACMPNE, ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void loadTracked(ServerWorld serverWorld, StructureManager structureManager, PointOfInterestStorage pointOfInterestStorage, ChunkPos chunkPos, CompoundTag compoundTag, CallbackInfoReturnable<ProtoChunk> cir,
                                    ChunkGenerator<?> chunkGenerator, BiomeSource biomeSource, CompoundTag compoundTag2, BiomeArray biomeArray, UpgradeData upgradeData, ChunkTickScheduler<Block> chunkTickScheduler,
                                    ChunkTickScheduler<Fluid> chunkTickScheduler2, boolean bl, ListTag listTag, int i, ChunkSection[] chunkSections, boolean bl2, ChunkManager chunkManager, LightingProvider lightingProvider,
                                    long l, ChunkStatus.ChunkType chunkType, Chunk chunk2) { // Holy FUCK so many locals just to get the one that I want
        if (compoundTag2.contains("TrackedBlocks", 10)) {
            CompoundTag trackedStates = compoundTag2.getCompound("TrackedBlocks");
            if (trackedStates.contains("TrackedStates", 11)) {
                ChunkTracker tracker = ChunkTracker.inject(chunk2);
                for (int s : trackedStates.getIntArray("TrackedStates")) {
                    if (trackedStates.contains(s + "", 12)) {
                        long[] trackedBlocks = trackedStates.getLongArray(s + "");
                        for (long pos : trackedBlocks) {
                            tracker.track(s, pos);
                        }
                    }
                }
            }
        }
    }
}
