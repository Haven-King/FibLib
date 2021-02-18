package dev.hephaestus.fiblib.blocks;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.mixin.blocks.ChunkReloader;
import dev.hephaestus.fiblib.mixin.blocks.TACSAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class LookupTable {
    private final Collection<ServerPlayerEntity> updated = new HashSet<>();
    private int updates = 0;
    private int version = 0;

    private final HashMap<ImmutablePair<BlockFib, ServerPlayerEntity>, BlockState>
            playerLookupTable = new HashMap<>();

    private void put(ImmutablePair<BlockFib, ServerPlayerEntity> key, BlockState input) {
        BlockState newState = key.left.getOutput(input, key.right);
        playerLookupTable.put(key, newState);
    }

    public BlockState get(BlockFib fib, BlockState input, ServerPlayerEntity player) {
        if (input != fib.getInput()) return input;

        ImmutablePair<BlockFib, ServerPlayerEntity> key = new ImmutablePair<>(fib, player);
        if (!playerLookupTable.containsKey(key)) {
            put(key, input);
        }

        return playerLookupTable.get(key);
    }

    public int getVersion() { return version; }

    public void update() {
        playerLookupTable.entrySet().removeIf(entry -> entry.getKey().right.isDisconnected());
        updated.clear();

        for (ImmutablePair<BlockFib, ServerPlayerEntity> key :  playerLookupTable.keySet()) {
            BlockState newState = key.left.getOutput(key.left.getInput(), key.right);
            BlockState oldState = playerLookupTable.get(key);

            if (newState != oldState) {
                playerLookupTable.put(key, newState);
                updated.add(key.right);
                ++updates;
            }
        }

        for (ServerPlayerEntity player : updated) {
            ThreadedAnvilChunkStorage TACS = ((TACSAccessor) player.getServerWorld().getChunkManager()).getThreadedAnvilChunkStorage();
            ((ChunkReloader) TACS).reloadChunks(player, true);
        }

        if (updates > 0) {
            FibLib.debug("Updating %d items. New version: %d", updates, ++version);
            updates = 0;
        }
    }

}
