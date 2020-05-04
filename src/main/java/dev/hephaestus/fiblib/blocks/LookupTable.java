package dev.hephaestus.fiblib.blocks;

import dev.hephaestus.fiblib.FibLib;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.HashMap;

public class LookupTable {
    private int updates = 0;
    private int version = 0;

    private final HashMap<ImmutablePair<BlockFib, ServerPlayerEntity>, BlockState>
            playerLookupTable = new HashMap<>();

    private void put(ImmutablePair<BlockFib, ServerPlayerEntity> key, BlockState input) {
        BlockState newState = key.left.getOutput(input, key.right);
        BlockState oldState = playerLookupTable.put(key, newState);
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
        for (ImmutablePair<BlockFib, ServerPlayerEntity> key :  playerLookupTable.keySet()) {
            BlockState newState = key.left.getOutput(key.left.getInput(), key.right);
            BlockState oldState = playerLookupTable.get(key);

            if (newState != oldState) {
                playerLookupTable.put(key, newState);
                FibLib.debug("Updating %s. Now: %s", oldState.getBlock().getTranslationKey(), newState.getBlock().getTranslationKey());
                ++updates;
            }
        }

        if (updates > 0) {
            FibLib.debug("Updating %d items. New version: %d", updates, ++version);
            updates = 0;
        }
    }

}
