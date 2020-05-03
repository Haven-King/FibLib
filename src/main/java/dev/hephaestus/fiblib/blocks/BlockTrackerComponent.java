package dev.hephaestus.fiblib.blocks;

import dev.hephaestus.fiblib.FibLib;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.Component;
import nerdhub.cardinal.components.api.component.extension.CopyableComponent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlockTrackerComponent implements TrackerComponent, CopyableComponent {
    public final Chunk chunk;

    private HashMap<Integer, LongSet> trackedBlocks = new HashMap<>();
    private int version = -1;

    public BlockTrackerComponent(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public Set<Integer> trackedStates() {
        return trackedBlocks.keySet();
    }

    @Override
    public HashMap<Integer, LongSet> tracked() {
        return trackedBlocks;
    }

    @Override
    public LongSet tracked(BlockState state) {
        return trackedBlocks.getOrDefault(Block.STATE_IDS.getId(state), new LongOpenHashSet());    }

    @Override
    public void track(BlockState state, BlockPos pos) {
        long posLong = pos.asLong();
        Integer id = Block.STATE_IDS.getId(state);

        if (FibLib.Blocks.contains(state)) {
            trackedBlocks.putIfAbsent(id, new LongOpenHashSet());
            trackedBlocks.get(id).add(posLong);
        } else if (trackedBlocks.containsKey(id) && trackedBlocks.get(id).contains(posLong)) {
            trackedBlocks.get(id).remove(posLong);
        }
    }

    @Override
    public void remove(BlockState state, BlockPos pos) {
        LongSet set = trackedBlocks.getOrDefault(Block.STATE_IDS.getId(state), new LongOpenHashSet());
        set.remove(pos.asLong());
    }

    public void track(int id, long pos) {
        trackedBlocks.putIfAbsent(id, new LongOpenHashSet());
        trackedBlocks.get(id).add(pos);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void update() {
        ServerWorld world;
        if (this.chunk instanceof WorldChunk && this.version != FibLib.Blocks.getVersion() && !(world = (ServerWorld) ((WorldChunk)chunk).getWorld()).isClient) {
            int updates = 0;
            for (LongSet s : trackedBlocks.values()) {
                for (Long l : s) {
                    world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
                    ++updates;
                }
            }
            this.version = FibLib.Blocks.getVersion();
            FibLib.debug("[%d, %d]: Updating %d blocks", chunk.getPos().x, chunk.getPos().z, updates);
        }
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        if (compoundTag.contains("TrackedBlocks", 10)) {
            CompoundTag trackedStates = compoundTag.getCompound("TrackedBlocks");
            if (trackedStates.contains("TrackedStates", 11)) {
                for (int s : trackedStates.getIntArray("TrackedStates")) {
                    if (trackedStates.contains(s + "", 12)) {
                        long[] trackedBlocks = trackedStates.getLongArray(s + "");
                        for (long pos : trackedBlocks) {
                            track(Block.STATE_IDS.get(s), BlockPos.fromLong(pos));
                        }
                    }
                }
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        CompoundTag trackedStates = new CompoundTag();
        for (Map.Entry<Integer, LongSet> entry : tracked().entrySet()) {
            LongArrayTag trackedBlocks = new LongArrayTag(entry.getValue());
            trackedStates.put(String.valueOf(entry.getKey()), trackedBlocks);
        }

        int[] tracked = new int[trackedStates().size()];
        int i = 0;
        for (Integer integer : trackedStates())
            tracked[i++] = integer;

        trackedStates.putIntArray("TrackedStates", tracked);

        compoundTag.put("TrackedBlocks", trackedStates);

        return compoundTag;
    }

    @Override
    public void copyFrom(Component other) {
        BlockTrackerComponent component;
        if (other.getClass() == BlockTrackerComponent.class && (component = ((BlockTrackerComponent)other)).chunk.getPos() == chunk.getPos()) {
            for (Map.Entry<Integer, LongSet> e : component.trackedBlocks.entrySet()) {
                for (Long l : e.getValue()) {
                    track(e.getKey(), l);
                }
            }
        }
    }

    @Override
    public ComponentType getComponentType() {
        return FibLib.Blocks.TRACKER;
    }
}